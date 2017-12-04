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
                REST_Controller::HTTP_NOT_FOUND);
        }
        else {
            $output_query = $this->Model_group->get_location($group_id);
            if ($output_query) {
                $location = array();
                foreach($output_query as $item) {
                    $item->is_leader = $this->Model_group->is_leader($item->group_id,$this->id);
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
                REST_Controller::HTTP_NOT_FOUND);
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
        }
    }
}