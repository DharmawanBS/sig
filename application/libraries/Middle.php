<?php
defined('BASEPATH') OR exit('No direct script access allowed');
/**
 * Created by PhpStorm.
 * User: dharmawan
 * Date: 12/2/17
 * Time: 2:26 PM
 */

class Middle
{
    public static function access()
    {
        header('Access-Control-Allow-Origin: *');
        header("Access-Control-Allow-Headers: X-API-KEY, Origin, X-Requested-With, Content-Type, Accept, Access-Control-Request-Method, token");
        header("Access-Control-Allow-Methods: GET, POST, OPTIONS, PUT, DELETE");
        $method = $_SERVER['REQUEST_METHOD'];
        if ($method == "OPTIONS") {
            die();
        }
    }

    /**
     * set default timezone to Jakarta
     */
    public static function date_time()
    {
        date_default_timezone_set('Asia/Jakarta');
    }

    /**
     * get token from header
     * @return string
     */
    public static function get_token()
    {
        $headers = apache_request_headers();
        $token = NULL;
        foreach ($headers as $header => $value) {
            if ($header == 'token') {
                $token = $value;
                break;
            }
        }
        return $token;
    }

    /**
     * default API output
     *
     * @param       string          $msg
     * @param       array|string    $data
     * @return      array
     */
    public static function output($msg,$data,$type = NULL)
    {
        if (is_null($type)) {
            return array(
                'msg' => $msg,
                'DATA' => $data
            );
        }
        else {
            return $data;
        }
    }

    /**
     * default API output
     *
     * @param       string|array  $data
     * @return      bool
     */
    public static function mandatory($data)
    {
        if ($data === "") return FALSE;
        else if (is_null($data)) return FALSE;
        return TRUE;
    }

    /**
     * generate random string
     *
     * @return      string
     */
    public static function generate_random_string($start = NULL, $end = NULL)
    {
        //  define minimal string length, if start was not declared
        if (is_null($start)) $start = 100;

        //  define maximal string length if start was not declared
        if (is_null($end)) $end = 200;

        //  random number between $start and $end
        $length = rand($end, $start);
        $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
        $charactersLength = strlen($characters);
        $randomString = '';
        for ($i = 0; $i < $length; $i++) {
            $randomString .= $characters[rand(0, $charactersLength - 1)];
        }
        return $randomString;
    }

    /**
     * increment or decrement datetime / date with year, month, day, hour, minute, or second
     *
     * @param       string      $date
     * @param       string      $output
     * @param       string      $action     + | -
     * @param       int         $year
     * @param       int         $month
     * @param       int         $day
     * @param       int         $hour
     * @param       int         $minute
     * @param       int         $second
     * @return      false|string
     */
    public static function inc_dec_datetime($date,$output = "Y-m-d H:i:s",$action = '+',$year = 0,$month = 0,$day = 0,$hour = 0,$minute = 0,$second = 0) {
        if ($action === '+' || $action === '-') {
            $date = date($output,strtotime($date));

            //  increment / decrement by year
            if ($year !== 0) {
                $date = date($output, strtotime($date . $action . $year . " year"));
            }

            //  increment / decrement month
            if ($month !== 0) {
                $date = date($output, strtotime($date . $action . $month . " month"));
            }

            //  increment / decrement day
            if ($day !== 0) {
                $date = date($output, strtotime($date . $action . $day . " day"));
            }

            //  increment / decrement hour
            if ($hour !== 0) {
                $date = date($output, strtotime($date . $action . $hour . " hour"));
            }

            //  increment / decrement minute
            if ($minute !== 0) {
                $date = date($output, strtotime($date . $action . $minute . " minute"));
            }

            //  increment / decrement second
            if ($second !== 0) {
                $date = date($output, strtotime($date . $action . $second . " second"));
            }
        }
        return $date;
    }

    /**
     * check output boolean from database, t = TRUE, f = FALSE
     *
     * @param string $input
     * @return bool
     */
    public static function check_boolean($input = 'f')
    {
        return ($input === 't');
    }
}