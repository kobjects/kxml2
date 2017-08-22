// Test case contributed by Andy Bailey
 
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import junit.framework.TestCase;

import org.kxml2.wap.Wbxml;
import org.kxml2.wap.WbxmlSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public class TestWb extends TestCase 
{
	public void testWb() throws IllegalArgumentException,IllegalStateException, FileNotFoundException, IOException
	{
		File file=new File("compress.xml");
		
		WbxmlSerializer xs = new WbxmlSerializer();
		boolean compress=true;
		
		xs.setOutput(new FileOutputStream(file),null);
		//xs.setOutput(System.out,"UTF-8");
		//xs.docdecl();
		//xs.setPrefix("","http://www.hazlorealidad.com");
		//xs.startDocument("UTF-8",true);
		xs.startDocument(null,null);
		//xs.comment("Comment");
		xs.startTag(null,"root");
		xs.startTag(null,"y");
		xs.attribute(null,"name","value");
		xs.writeWapExtension(Wbxml.EXT_T_1,new Integer(2));
		xs.endTag(null,"y");
		xs.startTag(null,"y");
		xs.attribute(null,"name","value");
		xs.writeWapExtension(Wbxml.EXT_T_1,new Integer(2));
		xs.endTag(null,"y");
		xs.endTag(null,"root");
		xs.endDocument();
		xs.flush();
		 long len=file.length();
         System.out.println(len+" bytes");
	}

    // Using hex code units to be sure that the system charset does not affect the behavior
    private static final String EMOJI_CHAR = "\ud83d\ude48";

    private static final String XML_TO_PARSE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
            + "<resources attr='" + EMOJI_CHAR + "' attr_hex='&#x1f648;' attr_dec='&#128584;'>\n"
            + "  <![CDATA[This is CDATA, with " + EMOJI_CHAR + ".]]>\n"
            + "  <!-- This is a comment, with " + EMOJI_CHAR + ", to see how it goes -->\n"
            + "  <string>Emoji: " + EMOJI_CHAR + "&#x1f648;&#128584;</string>\n"
            + "</resources>\n";

    private static void checkParseBeyondBmp(XmlPullParser xpp) throws XmlPullParserException, IOException {
        while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
            switch (xpp.getEventType()) {
                case XmlPullParser.CDSECT:
                    assertTrue(xpp.getText().contains(EMOJI_CHAR));
                    break;
                case XmlPullParser.COMMENT:
                    assertTrue(xpp.getText().contains(EMOJI_CHAR));
                    break;
                case XmlPullParser.TEXT:
                    final String text = xpp.getText().replaceAll("[\\n\\r\\t ]+", "");
                    if (!text.isEmpty()) {
                        assertTrue(xpp.getText().contains(EMOJI_CHAR));
                    }
                    break;
                case XmlPullParser.ENTITY_REF:
                    assertEquals(EMOJI_CHAR, xpp.getText());
                    break;
                case XmlPullParser.START_TAG:
                    for (int i = 0; i < xpp.getAttributeCount(); i++) {
                        assertEquals(EMOJI_CHAR, xpp.getAttributeValue(i));
                    }
                    break;
            }
            xpp.nextToken();
        }
    }

    public void testParseBeyondBmpFromReader() throws XmlPullParserException, IOException {
        final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        final XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(XML_TO_PARSE));

        checkParseBeyondBmp(xpp);
    }

    public void testParseBeyondBmpInputStream() throws XmlPullParserException, IOException {
        final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        final XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new ByteArrayInputStream(XML_TO_PARSE.getBytes(StandardCharsets.UTF_8)), "utf-8");

        checkParseBeyondBmp(xpp);
    }

    private static final String EXPECTED_XML_SERIALIZATION = ""
            + "<!--Emoji: " + EMOJI_CHAR + "-->\n"
            + "<![CDATA[Emoji: " + EMOJI_CHAR + "]]>\n"
            + "<string attr=\"&#128584;\">Emoji: &#128584;</string>";

    private static void checkSerializeBeyondBmp(XmlSerializer serializer) throws IOException {
        final String text = "Emoji: " + EMOJI_CHAR;

        serializer.comment(text);
        serializer.text("\n");
        serializer.cdsect(text);
        serializer.text("\n");
        serializer.startTag(null, "string");
        serializer.attribute(null, "attr", EMOJI_CHAR);
        serializer.text(text);
        serializer.endTag(null, "string");
        serializer.endDocument();
    }

    public void testSerializeBeyondBmpToOutputStream() throws XmlPullParserException, IOException {
        final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final XmlSerializer serializer = factory.newSerializer();

        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        serializer.setOutput(os, "utf-8");

        checkSerializeBeyondBmp(serializer);

        assertEquals(EXPECTED_XML_SERIALIZATION, os.toString("utf-8"));
    }

    public void testSerializeBeyondBmpToWriter() throws XmlPullParserException, IOException {
        final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final XmlSerializer serializer = factory.newSerializer();

        final StringWriter writer = new StringWriter();
        serializer.setOutput(writer);

        checkSerializeBeyondBmp(serializer);

        assertEquals(EXPECTED_XML_SERIALIZATION, writer.toString());
    }
}

