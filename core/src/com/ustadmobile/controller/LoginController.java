/*
    This file is part of Ustad Mobile.

    Ustad Mobile Copyright (C) 2011-2014 UstadMobile Inc.

    Ustad Mobile is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version with the following additional terms:

    All names, links, and logos of Ustad Mobile and Toughra Technologies FZ
    LLC must be kept as they are in the original distribution.  If any new
    screens are added you must include the Ustad Mobile logo as it has been
    used in the original distribution.  You may not create any new
    functionality whose purpose is to diminish or remove the Ustad Mobile
    Logo.  You must leave the Ustad Mobile logo as the logo for the
    application to be used with any launcher (e.g. the mobile app launcher).

    If you want a commercial license to remove the above restriction you must
    contact us.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    Ustad Mobile is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

 */
package com.ustadmobile.controller;

import com.ustadmobile.app.Base64;
import com.ustadmobile.impl.HTTPResult;
import com.ustadmobile.impl.UstadMobileSystemImpl;
import com.ustadmobile.view.LoginView;
import com.ustadmobile.view.ViewFactory;
import java.io.IOException;
import java.util.Hashtable;

/**
 * 
 * @author varuna
 */
public class LoginController implements UstadController{
    
    //private LoginView view;
    public LoginView view;
    
    public LoginController() {
        
    }
    
    public static int authenticate(String username, String password, String url) throws IOException{
        Hashtable headers = new Hashtable();
        headers.put("X-Experience-API-Version", "1.0.1");
        String encodedUserAndPass="Basic "+ Base64.encode(username,
                    password);
        headers.put("Authorization", encodedUserAndPass);
        HTTPResult authResult = UstadMobileSystemImpl.getInstance().makeRequest(
                url, headers, null, "GET");
        return authResult.getStatus();

    }
    
    public void handleClickLogin(String username, String password) {
        String serverURL = 
                UstadMobileSystemImpl.getInstance().getAppPref("server");
        int result = 0;
        IOException ioe = null;
        
        try {
            result = LoginController.authenticate(username, password, serverURL);    
        }catch(IOException e) {
            ioe = e;
        }
        
        if(result != 200) {
            this.view.showDialog("Error", "Login failed: please try again");
        }else {
            //make a new catalog controller and show it for the users base directory
            //Add username to UserPreferences.
            CatalogController catalogController = new CatalogController();
            catalogController.show();
            
        }
    }
    
    public void show() {
        this.view = ViewFactory.makeLoginView();
        this.view.setController(this);
        this.view.show();
    }
    
    public void hide() {
        
    }
}
