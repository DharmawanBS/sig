<?php
/**
 * Created by PhpStorm.
 * User: dharmawan
 * Date: 12/2/17
 * Time: 3:45 PM
 */

class Model_friend extends CI_Model
{
    function __construct()
    {
        parent::__construct();

        $this->middle = new Middle();

        $this->middle->date_time();
        $this->date_time = date("Y-m-d H:i:s");
        $this->date = date("Y-m-d");
    }

    function get($id){
        $this->db->select('u.user_id,u.user_reg_datetime,u.user_last_status,u.user_display_name,u.user_photo,f.datetime');
        $this->db->where('f.user_main',$id);
        $this->db->where('f.user_friend = u.user_id');
        $query = $this->db->get('user u,friend f');
        $result = $query->result();

        if (sizeof($result) > 0) return $result;
        return FALSE;
    }

    function find_user($username){
        $this->db->select('user_id,user_reg_datetime,user_last_status,user_display_name,user_photo');
        $this->db->where('user_name',$username);
        $query = $this->db->get('user');
        $result = $query->result();

        if (sizeof($result) > 0) return $result;
        return FALSE;
    }

    function is_friend($id,$user_id){
        $this->db->where('user_main',$id);
        $this->db->where('user_friend',$user_id);
        return $this->db->count_all_results('friend');
    }

    function be_friend($data){
        $this->db->insert_batch('friend',$data);
    }

    function un_friend($id,$friend){
        $this->db->where('user_main',$id);
        $this->db->where('user_friend',$friend);
        $this->db->delete('friend');

        $this->db->where('user_friend',$id);
        $this->db->where('user_main',$friend);
        $this->db->delete('friend');
    }
}