<?php
require_once APPPATH.'/libraries/REST_Controller.php';
require_once APPPATH.'/libraries/JWT.php';
use \Firebase\JWT\JWT;
/**
 * Created by PhpStorm.
 * User: dharmawan
 * Date: 12/2/17
 * Time: 1:03 AM
 */

/**
 * @property Model_user $Model_user
 */

class User extends REST_Controller
{
    public function __construct()
    {
        parent::__construct();
        $this->load->model(array('Model_user'));
        $this->middle = new Middle();
        $this->middle->access();
        $this->middle->date_time();

        $this->token = $this->middle->get_token();

        $this->date_time = date("Y-m-d H:i:s");
        $this->date = date("Y-m-d");
    }

    function login_post()
    {
        $user_name = $this->input->post('username');
        $user_password = $this->input->post('password');

        if (!$this->middle->mandatory($user_name) || !$this->middle->mandatory($user_password)) {
            $this->response(
                $this->middle->output(
                    MSG_INVALID,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
        else {
            $id = $this->Model_user->login($user_name,$user_password);
            if ($id) {
                $token = $this->Model_user->is_login($id);
                if ($token) {
                    $this->response(
                        $this->middle->output(
                            MSG_OK,
                            $token
                        ),
                        REST_Controller::HTTP_OK);
                }
                else {
                    $date = new DateTime();
                    $iat = $date->getTimestamp();
                    $exp = $iat + 60 * DURATION;

                    //  generate token
                    $data = array(
                        'id' => $user_name,
                        'iat' => $iat,
                        'exp' => $exp
                    );
                    $token = JWT::encode($data, $this->middle->generate_random_string());

                    $data = array(
                        'user_token' => $token,
                        'user_token_exp' => $this->date_time
                    );

                    $this->Model_user->update($id,$data);

                    $this->response(
                        $this->middle->output(
                            MSG_OK,
                            $token
                        ),
                        REST_Controller::HTTP_OK);
                }
            }
            else {
                $this->response(
                    $this->middle->output(
                        MSG_FAILED,
                        NULL
                    ),
                    REST_Controller::HTTP_OK);
            }
        }
    }

    function register_post()
    {
        $user_name = $this->input->post('username');
        $user_password = $this->input->post('password');
        $user_display_name = $this->input->post('name');

        if (!$this->middle->mandatory($user_name) || !$this->middle->mandatory($user_password) || !$this->middle->mandatory($user_display_name) || $this->Model_user->not_valid($user_name)) {
            $this->response(
                $this->middle->output(
                    MSG_INVALID,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
        else {
            $data = array(
                'user_id' => $this->Model_user->generate_id('user','user_id'),
                'user_name' => $user_name,
                'user_password' => md5($user_password),
                'user_reg_datetime' => $this->date_time,
                'user_display_name' => $user_display_name
            );

            $this->Model_user->insert($data);
            $this->response(
                $this->middle->output(
                    MSG_OK,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
    }

    function check_username_post()
    {
        $user_name = $this->input->post('username');
        if (!$this->middle->mandatory($user_name)) {
            $this->response(
                $this->middle->output(
                    MSG_INVALID,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
        else {
            if ($this->Model_user->not_valid($user_name)) {
                $this->response(
                    $this->middle->output(
                        MSG_FAILED,
                        NULL
                    ),
                    REST_Controller::HTTP_OK);
            }
            else {
                $this->response(
                    $this->middle->output(
                        MSG_OK,
                        NULL
                    ),
                    REST_Controller::HTTP_OK);
            }
        }
    }

    function auth()
    {
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

    function logout_get()
    {
        $this->auth();
        $data = array(
            'user_token' => NULL,
            'user_token_exp' => NULL
        );

        $this->Model_user->update($this->id, $data);
        $this->response(
            $this->middle->output(
                MSG_OK,
                NULL
            ),
            REST_Controller::HTTP_OK);
    }

    function profil_get()
    {
        $this->auth();
        $output_query = $this->Model_user->get_user($this->id);
        if ($output_query) {
            $this->response(
                $this->middle->output(
                    MSG_OK,
                    $output_query[0]
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

    function edit_profil_post()
    {
        $this->auth();
        $user_name = $this->input->post('username');
        $user_password = $this->input->post('password');
        $user_display_name = $this->input->post('name');

        if (!$this->middle->mandatory($user_name) || !$this->middle->mandatory($user_password) || !$this->middle->mandatory($user_display_name) || $this->Model_user->not_valid($user_name,$this->id)) {
            $this->response(
                $this->middle->output(
                    MSG_INVALID,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
        else {
            $data = array(
                'user_name' => $user_name,
                'user_password' => md5($user_password),
                'user_display_name' => $user_display_name
            );

            $this->Model_user->update($this->id,$data);
            $this->response(
                $this->middle->output(
                    MSG_OK,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
    }

    function edit_status_post()
    {
        $this->auth();
        $status = $this->input->post('status');

        if (!$this->middle->mandatory($status)) {
            $this->response(
                $this->middle->output(
                    MSG_INVALID,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
        else {
            $data = array(
                'user_last_status' => $status
            );

            $this->Model_user->update($this->id,$data);
            $this->response(
                $this->middle->output(
                    MSG_OK,
                    NULL
                ),
                REST_Controller::HTTP_OK);
        }
    }
}