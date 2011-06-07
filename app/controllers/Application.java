package controllers;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import play.Logger;
import play.libs.WS;
import play.libs.XPath;
import play.mvc.*;


import models.*;
import sun.security.krb5.internal.Ticket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void getit() {

        List<Ticket> tickets = new ArrayList<Ticket>();
        int page = 1;

        while ( true ) {
            Document doc = WS.url("http://play.lighthouseapp.com/projects/57987-play-framework/tickets.xml?q=state:open&page="+page).get().getXml();


            List<Node> nodes = XPath.selectNodes("/tickets/ticket", doc);
            if (nodes.size()==0) {
                break;
            }
            for ( Node ticket : nodes) {
                Ticket t = new Ticket();
                t.number = Integer.parseInt( XPath.selectText("number", ticket) );
                t.version = Integer.parseInt( XPath.selectText("version", ticket) );
                t.title = XPath.selectText("title", ticket);

                tickets.add( t);
            }

            page++;
        }


        Collections.sort( tickets);

        render(tickets);
    }

    public static class Ticket implements Comparable{
        public int number;
        public int version;
        public String title;

        public int compareTo(Object _o) {
            Ticket o = (Ticket)_o;
            if (version>o.version) {
                return -1;
            }
            if (version<o.version) {
                return 1;
            }

            if (number>o.number) {
                return 1;
            }
            if (number<o.number) {
                return -1;
            }
            return 0;
        }
    }

}