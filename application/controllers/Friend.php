<?php
require_once APPPATH.'/libraries/REST_Controller.php';
require_once APPPATH.'/libraries/JWT.php';
/**
 * Created by PhpStorm.
 * User: dharmawan
 * Date: 12/2/17
 * Time: 3:44 PM
 */

/**
 * @property Model_friend $Model_friend
 * @property Model_user $Model_user
 */

class Friend extends REST_Controller
{
    public function __construct()
    {
        parent::__construct();
        $this->load->model(array('Model_friend','Model_user'));
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

    function get_friend_get()
    {
        $output_query = $this->Model_friend->get($this->id);
        if ($output_query) {
            $friend = array();
            foreach($output_query as $item) {
                array_push($friend,$item);
            }
            $this->response(
                $this->middle->output(
                    MSG_OK,
                    $friend
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

    function find_user_post()
    {
        $user_name = $this->input->post('username');
        if (!$this->middle->mandatory($user_name)) {
            $this->response(
                $this->middle->output(
                    MSG_INVALID,
                    NULL
                ),
                REST_Controller::HTTP_NOT_FOUND);
        }

        $output_query = $this->Model_friend->find_user($user_name);
        if ($output_query) {
            $user = array();
            foreach($output_query as $item) {
                $item['is_friend'] = $this->Model_friend->is_friend($this->id,$user->user_id);
                array_push($user,$item);
            }
            $this->response(
                $this->middle->output(
                    MSG_OK,
                    $user
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

    function be_friend_post()
    {
        $friend = $this->input->post('friend');
        if (!$this->middle->mandatory($friend)) {
            $this->response(
                $this->middle->output(
                    MSG_INVALID,
                    NULL
                ),
                REST_Controller::HTTP_NOT_FOUND);
        }
        else {
            $data = array();
            $temp = array(
                'user_main' => $this->id,
                'user_friend' => $friend,
                'datetime' => $this->date_time
            );
            array_push($data, $temp);
            $temp = array(
                'user_main' => $friend,
                'user_friend' => $this->id,
                'datetime' => $this->date_time
            );
            array_push($data, $temp);
            $this->Model_friend->be_friend($data);
            $this->response(
                $this->middle->output(
                    MSG_OK,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
    }

    function un_friend_post()
    {
        $friend = $this->input->post('friend');
        if (!$this->middle->mandatory($friend)) {
            $this->response(
                $this->middle->output(
                    MSG_INVALID,
                    NULL
                ),
                REST_Controller::HTTP_NOT_FOUND);
        }
        else {
            $this->Model_friend->un_friend($this->id, $friend);
            $this->response(
                $this->middle->output(
                    MSG_OK,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
    }
}