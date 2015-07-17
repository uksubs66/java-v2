/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.clientReportTypes;

import java.util.*;

/**
 *
 * @author Owner
 */
public class visibleForeTeesAnnouncements {

    public final String type = this.getClass().getSimpleName();
    public String title = "Announcements";
    public Boolean show_active;
    public Boolean highlight_active;

    public visibleForeTeesAnnouncements() {
    }

    public visibleForeTeesAnnouncements(String title) {
        this.title = title;
    }
    
    public visibleForeTeesAnnouncements(Boolean highlight_active) {
        this.highlight_active = highlight_active;
    }
    
    public visibleForeTeesAnnouncements(String title, Boolean highlight_active) {
        this.title = title;
        this.highlight_active = highlight_active;
    }
    
    public visibleForeTeesAnnouncements(String title, Boolean highlight_active, Boolean show_active) {
        this.title = title;
        this.highlight_active = highlight_active;
        this.show_active = show_active;
    }

}
