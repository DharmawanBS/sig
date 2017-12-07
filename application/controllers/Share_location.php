<?php
require_once APPPATH.'/libraries/REST_Controller.php';
require_once APPPATH.'/libraries/JWT.php';
/**
 * Created by PhpStorm.
 * User: dharmawan
 * Date: 12/2/17
 * Time: 8:44 PM
 */

/**
 * @property Model_group $Model_group
 * @property Model_user $Model_user
 */

class Share_location extends REST_Controller
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

    function get_loc_post()
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
            $output_query = $this->Model_group->get_location($group_id);
            if ($output_query) {
                $location = array();
                foreach($output_query as $item) {
                    $item->is_leader = $this->Model_group->is_leader($item->group_id,$item->user_id);
                    array_push($location,$item);
                }
                $this->response(
                    $this->middle->output(
                        MSG_OK,
                        $location
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
    }

    function update_loc_post()
    {
        $group_id = $this->input->post('group');
        $user_id = $this->input->post('user');
        $latitude = $this->input->post('latitude');
        $longitude = $this->input->post('longitude');

        if (!$this->middle->mandatory($group_id) ||
            !$this->middle->mandatory($user_id) ||
            !$this->middle->mandatory($latitude) ||
            !$this->middle->mandatory($longitude)) {
            $this->response(
                $this->middle->output(
                    MSG_INVALID,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
        else {
            $data = array(
                'group_id' => $group_id,
                'user_id' => $user_id,
                'loc_datetime' => $this->date_time,
                'loc_latitude' => $latitude,
                'loc_longitude' => $longitude
            );
            $this->Model_group->save_location($data);
            $this->response(
                $this->middle->output(
                    MSG_OK,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
    }

    function save_loc_post()
    {
        $group_id = $this->input->post('group');
        $title = $this->input->post('title');

        if (!$this->middle->mandatory($group_id) ||
            !$this->middle->mandatory($title)) {
            $this->response(
                $this->middle->output(
                    MSG_INVALID,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
        else {
            $id = $this->Model_user->generate_id('save_travel','travel_id');
            $data = array(
                'travel_id' => $id,
                'travel_title' => $title,
                'travel_datetime' => $this->date_time,
                'user_id' => $this->id
            );
            $this->Model_group->save_travel($data);

            $output_query = $this->Model_group->get_location($group_id);
            if ($output_query) {
                $location = array();
                foreach ($output_query as $item) {
                    $temp = array(
                        'travel_id' => $id,
                        'user_id' => $item->user_id,
                        'loc_datetime' => $item->loc_datetime,
                        'loc_latitude' => $item->loc_latitude,
                        'loc_longitude' => $item->loc_longitude
                    );
                    array_push($location, $temp);
                }
                $this->Model_group->save_location2($location);
            }
            $this->response(
                $this->middle->output(
                    MSG_OK,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
    }

    function get_loc2_post()
    {
        $travel_id = $this->input->post('travel');
        if (!$this->middle->mandatory($travel_id)) $travel_id = null;
        $output_query = $this->Model_group->get_save($this->id,$travel_id);
        if ($output_query) {
            $save = array();
            foreach ($output_query as $item) {
                $output_location = $this->Model_group->get_location2($item->travel_id);
                if ($output_location) {
                    $location = array();
                    foreach ($output_location as $item_loc) {
                        array_push($location, $item_loc);
                    }
                }
                else {
                    $location = null;
                }
                $item->location = $location;
                array_push($save, $item);
            }
            $this->response(
                $this->middle->output(
                    MSG_OK,
                    $save
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
}