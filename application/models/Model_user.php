<?php
defined('BASEPATH') OR exit('No direct script access allowed');
/**
 * Created by PhpStorm.
 * User: dharmawan
 * Date: 12/2/17
 * Time: 1:06 AM
 */

class Model_user extends CI_Model
{
    function __construct()
    {
        parent::__construct();

        $this->middle = new Middle();

        $this->middle->date_time();
        $this->date_time = date("Y-m-d H:i:s");
        $this->date = date("Y-m-d");
    }

    function login($name,$password,$token = NULL){
        $this->db->select('user_id');
        if(!is_null($token)) {
            $this->db->where('user_token', $token);
        }
        else {
            $this->db->where('user_name', $name);
            $this->db->where('user_password', md5($password));
        }
        $query = $this->db->get('user');
        $result = $query->result();

        if (sizeof($result) > 0) {
            $out = $result[0];
            return $out->user_id;
        }
        return FALSE;
    }

    function is_login($id){
        $this->db->select('user_token');
        $this->db->where('user_id',$id);
        $this->db->where('user_token_exp >',$this->date_time);
        $query = $this->db->get('user');
        $result = $query->result();

        if (sizeof($result) > 0) {
            $out = $result[0];
            return $out->user_token;
        }
        return FALSE;
    }

    function insert($data,$temp){
        $this->db->insert('user',$data);
        $this->db->insert('group',$temp);
    }

    function update($id,$data){
        $this->db->where('user_id',$id);
        $this->db->update('user',$data);
    }

    function get_user($id){
        $this->db->select('user_id,user_name,user_reg_datetime,user_last_status,user_display_name,user_photo');
        $this->db->where('user_id',$id);
        $query = $this->db->get('user');
        $result = $query->result();

        if (sizeof($result) > 0) return $result;
        return FALSE;
    }

    function not_valid($username,$id = null){
        $this->db->where('user_name',$username);
        if (!is_null($id)) $this->db->where('user_id !=',$id);
        return $this->db->count_all_results('user');
    }

    function generate_id($table,$table_id){
        $this->db->select($table_id);
        $this->db->order_by($table_id,'DESC');
        $this->db->limit(1);
        $query = $this->db->get($table);
        $result = $query->result();

        if (sizeof($result) > 0) {
            $out = $result[0];
            return ($out->$table_id)+1;
        }
        return 1;
    }
}