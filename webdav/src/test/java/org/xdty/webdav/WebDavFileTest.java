package org.xdty.webdav;

import org.junit.Test;

import java.net.URISyntaxException;

import static junit.framework.TestCase.assertEquals;

public class WebDavFileTest {

    @Test
    public void urlTest() {
        try {
            WebDavFile webDavFile = new WebDavFile("dav://example.com");
            assertEquals(webDavFile.getPath(), "dav://example.com");
            assertEquals(webDavFile.getHost(), "example.com");
            assertEquals(webDavFile.getName(), "");

            webDavFile = new WebDavFile("dav://example.com/usb/");
            assertEquals(webDavFile.getPath(), "dav://example.com/usb/");
            assertEquals(webDavFile.getHost(), "example.com");
            assertEquals(webDavFile.getName(), "usb");

            webDavFile = new WebDavFile("dav://example.com/usb/aaa.txt");
            webDavFile.setParent("dav://example.com/usb/");
            assertEquals(webDavFile.getPath(), "dav://example.com/usb/aaa.txt");
            assertEquals(webDavFile.getHost(), "example.com");
            assertEquals(webDavFile.getName(), "aaa.txt");

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
