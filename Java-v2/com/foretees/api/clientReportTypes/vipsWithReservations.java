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
public class vipsWithReservations {

    public final String type = this.getClass().getSimpleName();
    public String date;
    public String title;
    public Integer days;

    public vipsWithReservations() {
    }

    public vipsWithReservations(String date, String title, Integer days) {

        this.date = date;
        this.title = title;
        this.days = days;

    }

}
