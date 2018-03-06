package task_05.osm;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapParser {

    public static Map parse(String filepath) {
        File file = new File(filepath);
        if (!file.exists()) {
            throw new RuntimeException("File doesn't exist!");
        }

        System.out.format("will parse osm file at '%s'\n", file.getAbsolutePath());


        List<Element> elements = new ArrayList<>();

        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

            DefaultHandler handler = new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

                    Element element = null;

                    switch (qName) {
                        case "node":
                            element = new MapNode(
                                    Long.valueOf(attributes.getValue("id")),
                                    Double.valueOf(attributes.getValue("lat")),
                                    Double.valueOf(attributes.getValue("lon"))
                            );
                            break;

                        case "tag":
                            // tags always belong to the last element we processed
                            String key = attributes.getValue("k");
                            String value = attributes.getValue("v");
                            elements.get(elements.size() - 1).addTag(new Element.Tag(key, value));
                            break;

                        case "way":
                            elements.add(new Way(Long.valueOf(attributes.getValue("id"))));
                            break;

                        case "nd":
                            // reference to a node that is part of a way
                            Element lastElement = elements.get(elements.size() - 1);

                            if (lastElement instanceof Way) {
                                ((Way) lastElement).addNodeId((Long.valueOf(attributes.getValue("ref"))));
                            }
                    }


                    if (element != null) {
                        elements.add(element);
                    }
                }
            };

            parser.parse(file, handler);

            return new Map(elements);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
