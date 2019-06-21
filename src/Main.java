import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.*;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import nl.siegmann.epublib.domain.Resource;

import nl.siegmann.epublib.epub.EpubWriter;

import javax.swing.text.html.HTMLDocument;

import static nl.siegmann.epublib.service.MediatypeService.XHTML;


public class Main {
    public static void main(String[] args) throws IOException {
        EpubReader epubReader = new EpubReader();
        ArrayList<String> filePaths = new ArrayList<>();

        try {
            File file = new File("EISBNS.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNext()) {
                String filePath = scanner.nextLine();
                filePaths.add(filePath);
            }
        } catch (
                Exception e) {
            System.out.println("Problem reading the file. Please make sure the path is correct");
        }

        for (String filePath : filePaths) {


            Book book = epubReader.readEpub(new FileInputStream(filePath));

            Document toc = new Document("www.example.com");


            Resources resources = book.getResources();

            List<Resource> htmlFiles = resources.getResourcesByMediaType(XHTML);
            for (Resource resource : htmlFiles) {
                String testo = resource.getHref();
                if (testo.contains("_con01")) {
                    InputStream tocInput = resource.getInputStream();
                    toc = Jsoup.parse(tocInput, "ISO-8859-1", "www.example.com");
                }
            }

            Elements tocEntries = toc.select("a[href]");


            for (Resource resource : htmlFiles) {
                InputStream htmlInput = resource.getInputStream();
                Document doc = Jsoup.parse(htmlInput, "UTF-8", "www.example.com");


//        File input = new File("C:/Users/bylander/Desktop/TOC_pull_WORK/new_unzipped_final_epubs/9780807770115/OEBPS/talkingtheirwayintoscience_con01.html");
//        Document doc = Jsoup.parse(input, "UTF-8");

                Elements elementsWithIds = doc.select("*[id]");
                Elements links = doc.select("a");
                Elements media = doc.select("[src]");
                Elements imports = doc.select("link[href]");

                for (Element idElement : elementsWithIds) {
                    int index = elementsWithIds.indexOf(idElement);
                    print(" * Element with ID: <%s> <%s>", idElement.id(), index);
                }

                if (elementsWithIds.size() > 2 && elementsWithIds.get(1).equals(doc.select("div.part").first())) {
                    Element chapNameElement = elementsWithIds.get(1);
                    Element chapPageElement = elementsWithIds.get(3);

                    for (Element link : tocEntries) {
                        String anchorFromTOC = link.attr("href").substring(link.attr("href").lastIndexOf("#") + 1);
                        if (anchorFromTOC.equals(chapNameElement.id())) {
                            String pageNumberID = chapPageElement.id();
                            String numberOnly = pageNumberID.substring(chapPageElement.id().lastIndexOf("e") + 1);
                            link.parent().addClass("level1");
                            link.appendText(Jsoup.parse("&#x2003;").text() + numberOnly);
                            print(" * a: <%s> <%s>  %s", link.id(), link.attr("href"), link.text());
                        } else {
                            for (Element idElement : elementsWithIds) {
                                if (idElement.id().equals(anchorFromTOC)) {
                                    Element aHeadID = elementsWithIds.select("#" + anchorFromTOC).get(0);
                                    String dodo = "dodo";
                                    int index = elementsWithIds.indexOf(aHeadID);
                                    do {
                                        index--;
                                    } while (!elementsWithIds.get(index).id().contains("page"));
                                    Element bobo = link.parent();
                                    link.parent().addClass("level2");
                                    dodo = "dada";
                                    link.appendText(Jsoup.parse("&#x2003;").text() + elementsWithIds.get(index).id().substring(elementsWithIds.get(index).id().lastIndexOf("e") + 1));
                                    print(" * a: <%s> <%s>  %s", link.id(), link.attr("href"), link.text());

                                }
                            }
                        }
                    }
                } else if ((elementsWithIds.size() > 2 && !elementsWithIds.get(1).equals(doc.select("div.part").first()))) {
                    Element chapNameElement = elementsWithIds.get(1);
                    Element chapPageElement = elementsWithIds.get(2);

                    for (Element link : tocEntries) {
                        String anchorFromTOC = link.attr("href").substring(link.attr("href").lastIndexOf("#") + 1);
                        if (anchorFromTOC.equals(chapNameElement.id())) {
                            String pageNumberID = chapPageElement.id();
                            String numberOnly = pageNumberID.substring(chapPageElement.id().lastIndexOf("e") + 1);
                            link.parent().addClass("level1");
                            link.appendText(Jsoup.parse("&#x2003;").text() + numberOnly);
                            print(" * a: <%s> <%s>  %s", link.id(), link.attr("href"), link.text());
                        } else {
                            for (Element idElement : elementsWithIds) {
                                if (idElement.id().equals(anchorFromTOC)) {
                                    Element aHeadID = elementsWithIds.select("#" + anchorFromTOC).get(0);
                                    String dodo = "dodo";
                                    int index = elementsWithIds.indexOf(aHeadID);
                                    do {
                                        index--;
                                    } while (!elementsWithIds.get(index).id().contains("page"));
                                    Element bobo = link.parent();
                                    link.parent().addClass("level2");
                                    dodo = "dada";
                                    link.appendText(Jsoup.parse("&#x2003;").text() + elementsWithIds.get(index).id().substring(elementsWithIds.get(index).id().lastIndexOf("e") + 1));
                                    print(" * a: <%s> <%s>  %s", link.id(), link.attr("href"), link.text());

                                }
                            }
                        }
                    }
                }


//                archiveAllTags(filePath, toc);
                archiveSimpleTOC(filePath, toc);




//            print("\nMedia: (%d)", media.size());
//            for (Element src : media) {
//                if (src.tagName().equals("img"))
//                    print(" * %s: <%s> %sx%s (%s)",
//                            src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
//                            trim(src.attr("alt"), 20));
//                else
//                    print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
//            }
//
//            print("\nImports: (%d)", imports.size());
//            for (Element link : imports) {
//                print(" * %s <%s> (%s)", link.tagName(), link.attr("abs:href"), link.attr("rel"));
//            }
//
//            print("\nLinks: (%d)", links.size());
//            for (Element link : links) {
//                print(" * a: <%s> <%s>  (%s)", link.id(), link.attr("href"), trim(link.text(), 35));
//            }





            }


        }
    }


    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width - 1) + ".";
        else
            return s;
    }

    private static void archiveAllTags (String filePath, Document toc) throws IOException {

        String tocArchive = "C:/Users/bylander/Desktop/New TOC archive from epub 06172019/";
        final File f = new File(tocArchive + filePath.substring((filePath.lastIndexOf("/") + 1), filePath.lastIndexOf(".")) + "_TOC.html");
        FileUtils.writeStringToFile(f, toc.outerHtml(), "UTF-8");

    }

    private static void archiveSimpleTOC (String filePath, Document toc) throws IOException {

        Elements tocContentsEntries = toc.getAllElements();

        tocContentsEntries.select(".italic").tagName("em");
        tocContentsEntries.select(".bold").tagName("strong");
        Elements levelOneEntries =  tocContentsEntries.select(".level1");
        Elements levelTwoEntries =  tocContentsEntries.select(".level2");
//        for (Element twoE : levelTwoEntries) {
//            twoE.append("<br />");
//        }
//        levelTwoEntries.unwrap();
//        levelOneEntries.tagName("p");

        Elements allElements = toc.getAllElements();

//        List<String>  attToRemove = new ArrayList<>();
//        for (Element entry : allElements) {
//            Attributes at = entry.attributes();
//            for (Attribute a : at) {
//                attToRemove.add(a.getKey());
//            }
//
//            for(String att : attToRemove) {
//                entry.removeAttr(att);
//                String dod = "dad";
//            }
//        }


        String tocArchive = "C:/Users/bylander/Desktop/New TOC archive from epub 06172019/Txt files/";
        final File f = new File(tocArchive + filePath.substring((filePath.lastIndexOf("/") + 1), filePath.lastIndexOf(".")) + "_TOC.txt");
        FileUtils.writeStringToFile(f, toc.body().child(0).outerHtml(), "UTF-8");

    }

//    private boolean notEveryElementHasId() throws IOException {
//        for (Resource resource : resources) {
//            document = Jsoup.parse(contentBuilder.getStringFromInputStream(resource.getInputStream()));
//            if (!document.getAllElements().isEmpty()) {
//                Elements allElements = document.getAllElements();
//                if (getFirstIdElement(allElements) != null) {
//                    Element firstIdElement = getFirstIdElement(allElements);
//                    for (int i = allElements.indexOf(firstIdElement); i < allElements.size(); i++) {
//
//                        if (!allElements.get(i).hasAttr(Constants.ID)) {
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//        return false;
//    }
}


