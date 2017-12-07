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

    function insert($data){
        $this->db->insert('group',$data);
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

    function be_leader($id,$group){
        $this->db->where('user_id',$id);
        $this->db->where('group_id',$group);
        $this->db->delete('group_leader');

        $data = array(
            'user_id' => $id,
            'group_id' => $group
        );
        $this->db->insert('group_leader',$data);
    }

    function find_id($group_name){
        $this->db->select('group_id');
        $this->db->where('group_name',$group_name);
        $query = $this->db->get('group');
        $result = $query->result();

        if (sizeof($result) > 0) {
            $out = $result[0];
            return $out->group_id;
        }
        return FALSE;
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
        $this->db->select('u.user_id,u.user_display_name,u.user_photo,gsl.*');
        $this->db->where('gsl.group_id',$group);
        $this->db->where('gsl.user_id = u.user_id');
        $this->db->order_by('gsl.loc_datetime','DESC');
        $this->db->order_by('u.user_display_name','DESC');
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

    function is_leader($group,$id){
        $this->db->where('user_id',$id);
        $this->db->where('group_id',$group);
        if ($this->db->count_all_results('group_leader') > 0) return true;
        return false;
    }

    function save_travel($data){
        $this->db->insert('save_travel',$data);
    }

    function save_location2($data){
        $this->db->insert_batch('location_save',$data);
    }

    function get_save($id,$travel=null){
        $this->db->select('travel_id,travel_title,travel_datetime');
        $this->db->where('user_id',$id);
        if(!is_null(($travel))) $this->db->where('travel_id',$travel);
        $query = $this->db->get('save_travel');
        $result = $query->result();

        if (sizeof($result) > 0) return $result;
        return FALSE;
    }

    function get_location2($travel){
        $this->db->select('u.user_id,u.user_display_name,u.user_photo,ls.*');
        $this->db->where('ls.travel_id',$travel);
        $this->db->where('ls.user_id = u.user_id');
        $this->db->order_by('ls.loc_datetime','DESC');
        $this->db->order_by('u.user_display_name','DESC');
        $query = $this->db->get('user u,location_save ls');
        $result = $query->result();

        if (sizeof($result) > 0) return $result;
        return FALSE;
    }
}