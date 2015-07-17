/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api;

import com.foretees.api.records.ClubSetting;
import com.foretees.common.ArrayUtil;
import com.foretees.common.Connect;
import com.foretees.common.ProcessConstants;
import com.foretees.common.reqUtil;
import java.sql.*;          // mysql
//import java.util.UUID;
//import javax.naming.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author Owner
 */
public class ApiClubSetting {
    
    private Map<String, ClubSetting> setting_cache = new HashMap<String, ClubSetting>();
    
}
