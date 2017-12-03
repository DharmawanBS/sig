<?php
/**
 * Created by PhpStorm.
 * User: dharmawan
 * Date: 12/2/17
 * Time: 7:29 PM
 */

class Model_group extends CI_Model
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
        $this->db->select('g.*');
        $this->db->where('gu.user_id',$id);
        $this->db->where('g.group_id = gu.group_id');
        $query = $this->db->get('group g,group_user gu');
        $result = $query->result();

        if (sizeof($result) > 0) return $result;
        return FALSE;
    }

    function join($data){
        $this->db->insert('group_user',$data);
    }

    function left($id,$group){
        $this->db->where('user_id',$id);
        $this->db->where('group_id',$group);
        $this->db->delete('group_user');
    }

    function find_group($id){
        $this->db->select('*');
        $this->db->where('group_id',$id);
        $query = $this->db->get('group');
        $result = $query->result();

        if (sizeof($result) > 0) return $result;
        return FALSE;
    }

    function is_joined($id,$group_id){
        $this->db->where('user_id',$id);
        $this->db->where('group_id',$group_id);
        return $this->db->count_all_results('group_user');
    }

    function find_leader($id){
        $this->db->select('u.user_id,u.user_reg_datetime,u.user_last_status,u.user_display_name,user_photo');
        $this->db->where('gl.group_id',$id);
        $this->db->where('u.user_id = gl.user_id');
        $query = $this->db->get('user u,group_leader gl');
        $result = $query->result();

        if (sizeof($result) > 0) return $result;
        return FALSE;
    }

    function save_location($data){
        $this->db->insert('group_share_loc',$data);
    }

    function get_location($group){
        $this->db->select('u.user_photo,gsl.*');
        $this->db->where('gsl.group_id',$group);
        $this->db->where('gsl.user_id = u.user_id');
        $query = $this->db->get('user u,group_share_loc gsl');
        $result = $query->result();

        if (sizeof($result) > 0) return $result;
        return FALSE;
    }

    function member($group){
        $this->db->select('u.user_id,u.user_reg_datetime,u.user_last_status,u.user_display_name,user_photo');
        $this->db->where('gu.group_id',$group);
        $this->db->where('u.user_id = gu.user_id');
        $query = $this->db->get('user u,group_user gu');
        $result = $query->result();

        if (sizeof($result) > 0) return $result;
        return FALSE;
    }
}