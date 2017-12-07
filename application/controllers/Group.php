<?php
require_once APPPATH.'/libraries/REST_Controller.php';
require_once APPPATH.'/libraries/JWT.php';
/**
 * Created by PhpStorm.
 * User: dharmawan
 * Date: 12/2/17
 * Time: 7:28 PM
 */

/**
 * @property Model_group $Model_group
 * @property Model_user $Model_user
 */

class Group extends REST_Controller
{
    public function __construct()
    {
        parent::__construct();
        $this->load->model(array('Model_group','Model_user'));
        $this->middle = new Middle();
        $this->middle->access();
        $this->middle->date_time();

        $this->token = $this->middle->get_token();

        $this->date_time = date("Y-m-d H:i:s");
        $this->date = date("Y-m-d");

        if ($this->token == NULL) {
            $this->response(
                $this->middle->output(
                    MSG_UNAUTHORIZED,
                    NULL),
                REST_Controller::HTTP_UNAUTHORIZED);
        }
        else {
            $this->id = $this->Model_user->login(NULL,NULL,$this->token);
            if (!$this->id) {
                $this->response(
                    $this->middle->output(
                        MSG_UNAUTHORIZED,
                        NULL),
                    REST_Controller::HTTP_UNAUTHORIZED);
            }
        }
    }

    function get_group_get()
    {
        $output_query = $this->Model_group->get($this->id);
        if ($output_query) {
            $group = array();
            foreach($output_query as $item) {
                $output_query_leader = $this->Model_group->find_leader($item->group_id);
                if ($output_query_leader) {
                    $leader = array();
                    foreach ($output_query_leader as $item_leader) {
                        array_push($leader, $item_leader);
                    }
                }
                else {
                    $leader = NULL;
                }
                $output_query_member = $this->Model_group->member($item->group_id);
                if ($output_query_member) {
                    $member = array();
                    foreach ($output_query_member as $item_member) {
                        array_push($member, $item_member);
                    }
                }
                else {
                    $member = NULL;
                }
                $item->leader = $leader;
                $item->member = $member;
                array_push($group,$item);
            }
            $this->response(
                $this->middle->output(
                    MSG_OK,
                    $group
                ),
                REST_Controller::HTTP_OK);
        }
        else {
            $this->response(
                $this->middle->output(
                    MSG_EMPTY,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
    }

    function find_group_post()
    {
        $group_name = $this->input->post('group');
        if (!$this->middle->mandatory($group_name)) {
            $this->response(
                $this->middle->output(
                    MSG_INVALID,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
        else {
            $group_id = $this->Model_group->find_id($group_name);
            if ($group_id) {
                $output_query = $this->Model_group->find_group($group_id);
                if ($output_query) {
                    $group = array();
                    foreach ($output_query as $item) {
                        $item['is_joined'] = $this->Model_group->is_joined($this->id, $group_id);
                        array_push($group, $item);
                    }
                    $this->response(
                        $this->middle->output(
                            MSG_OK,
                            $group
                        ),
                        REST_Controller::HTTP_OK);
                }
                else {
                    $this->response(
                        $this->middle->output(
                            MSG_EMPTY,
                            NULL
                        ),
                        REST_Controller::HTTP_OK);
                }
            }
            else {
                $this->response(
                    $this->middle->output(
                        MSG_EMPTY,
                        NULL
                    ),
                    REST_Controller::HTTP_OK);
            }
        }
    }

    function join_group_post()
    {
        $group_id = $this->input->post('group');
        if (!$this->middle->mandatory($group_id)) {
            $this->response(
                $this->middle->output(
                    MSG_INVALID,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
        else {
            $data = array(
                'user_id' => $this->id,
                'group_id' => $group_id,
                'datetime' => $this->date_time
            );
            $this->Model_group->join($data);
            $this->response(
                $this->middle->output(
                    MSG_OK,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
    }

    function left_group_post()
    {
        $group_id = $this->input->post('group');
        if (!$this->middle->mandatory($group_id)) {
            $this->response(
                $this->middle->output(
                    MSG_INVALID,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
        else {
            $this->Model_group->left($this->id, $group_id);
            $this->response(
                $this->middle->output(
                    MSG_OK,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
    }

    function be_leader_post()
    {
        $group_id = $this->input->post('group');
        if (!$this->middle->mandatory($group_id)) {
            $this->response(
                $this->middle->output(
                    MSG_INVALID,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
        else {
            $this->Model_group->be_leader($this->id, $group_id);
            $this->response(
                $this->middle->output(
                    MSG_OK,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
    }

    function create_group_post()
    {
        $name = $this->input->post('name');
        if (!$this->middle->mandatory($name)) {
            $this->response(
                $this->middle->output(
                    MSG_INVALID,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
        else {
            $data = array(
                'group_id' => $this->Model_user->generate_id('group','group_id'),
                'group_name' => $name,
                'group_create_datetime' => $this->date_time,
                'group_create_by' => $this->id
            );
            $this->Model_group->insert($data);
            $this->response(
                $this->middle->output(
                    MSG_OK,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
    }
}