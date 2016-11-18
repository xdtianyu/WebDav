package org.xdty.webdav;

import org.junit.Test;

import java.net.MalformedURLException;

import static junit.framework.TestCase.assertEquals;

public class WebDavFileTest {

    @Test
    public void urlTest() throws MalformedURLException {
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

        webDavFile = new WebDavFile("davs://example.com/usb/11/1118/[哈哈哈] 哦哦哦");
        webDavFile.setParent("davs://example.com/usb/11/1118/");
        assertEquals(webDavFile.getPath(), "davs://example.com/usb/11/1118/[哈哈哈] 哦哦哦");
        assertEquals(webDavFile.getName(), "[哈哈哈] 哦哦哦");

        webDavFile = new WebDavFile("davs://example.com/usb/11/1118/[哈哈哈]+哦哦哦");
        webDavFile.setParent("davs://example.com/usb/11/1118/");
        assertEquals(webDavFile.getPath(), "davs://example.com/usb/11/1118/[哈哈哈]+哦哦哦");
        assertEquals(webDavFile.getName(), "[哈哈哈]+哦哦哦");

        webDavFile = new WebDavFile("davs://example.com/usb/11/1118/[哈哈哈] 哦哦哦/万维网.jpg");
        webDavFile.setParent("davs://example.com/usb/11/1118/[哈哈哈] 哦哦哦/");
        assertEquals(webDavFile.getPath(), "davs://example.com/usb/11/1118/[哈哈哈] 哦哦哦/万维网.jpg");
        assertEquals(webDavFile.getName(), "万维网.jpg");

        webDavFile = new WebDavFile("davs://example.com/usb/11/1118/[哈哈哈] 哦哦哦/万维#网.jpg");
        webDavFile.setParent("davs://example.com/usb/11/1118/[哈哈哈] 哦哦哦/");
        assertEquals(webDavFile.getPath(), "davs://example.com/usb/11/1118/[哈哈哈] 哦哦哦/万维#网.jpg");
        assertEquals(webDavFile.getName(), "万维#网.jpg");
    }
}
