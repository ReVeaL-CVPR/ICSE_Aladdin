package com.ias.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.robotium.solo.Solo;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by vector on 16/5/10.
 */
public class HtmlTranslator {
    public static HtmlTranslator htmlTranslator;
    Solo solo;

    public HtmlTranslator(Solo solo) {
        this.solo = solo;
    }

    public String toHTML() {
        return toHTML(solo.getView(android.R.id.content));
    }

    public String toHTML(View root) {
        Log.v("Ruogu", "To Html");
        String o_str = "";
        o_str += "<!DOCTYPE html>\n<html>\n" +
                "<head>" + "<title>" +
                solo.getCurrentActivity().getIntent().getPackage() + "</title>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>" + "</head>\n";
        o_str += "<body>\n";
        o_str += "<link type=\"text/css\" rel=\"stylesheet\" href=\"1.css\"/>\n";
        o_str += __toHTML__(root);
        o_str += "\t</body>\n</html>\n";

        try {
            FileHelper.writeFile(System.currentTimeMillis() + ".html", o_str, solo.getCurrentActivity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return o_str;
    }

    private boolean isDerivedFrom(Class c, Class p) {
        while (c != Object.class) {
            if (c == p)
                return true;
            c = c.getSuperclass();
        }
        return false;
    }

    public String __toHTML__(View root) {
        String o_str = "";
        o_str += "<div style=\"";
        o_str += put_height_width(root);
        o_str += put_position(root);
        o_str += "\">\n";
        if (isDerivedFrom(root.getClass(), TextView.class))
            o_str += put_text((TextView) root);
        if (isDerivedFrom(root.getClass(), WebView.class))
            o_str += put_web((WebView) root);
        List<View> list = solo.getViews(root);
        Iterator<View> itr = list.iterator();
        while (itr.hasNext()) {
            View v = itr.next();
            if (v.getParent() == root)
                o_str += __toHTML__(v);
        }
        o_str += "</div>\n";
        return o_str;
    }

    public String put_height_width(View root) {
        String o_str = "";
        o_str += "height: " + root.getHeight() + "px; ";
        o_str += "width: " + root.getWidth() + "px; ";
        return o_str;
    }

    public String put_text(TextView root) {
        String o_str = "";
        o_str += "\t<p style=\"font-size: "
                + root.getTextSize() + "px\""
                + ">" + "<pre>" + root.getText() + "</pre>" + "</p>\n";
        return o_str;
    }

    public String put_position(View root) {
        String o_str = "";
        o_str += "position: absolute; ";
        o_str += "left: " + root.getX() + "px; ";
        o_str += "top: " + root.getY() + "px; ";
        return o_str;
    }

    public String put_web(WebView root) {
        solo.sleep(1000);
        String a = solo.getHTML();
        Log.i("liuyi", a);
        getCurrentWebContent(root);
        solo.sleep(1000);
        a = solo.getHTML();
        return "<!DOCTYPE html>\n" + a;
    }

    public void getCurrentWebContent(View view) {
        final WebView content_wv = (WebView) view;
        Activity cur = solo.getCurrentActivity();
        cur.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                content_wv.loadUrl("javascript:prompt('ias' + document.getElementsByTagName(\"html\")[0].outerHTML);");
            }
        });
    }
}

