package ru.java.mentor.oldranger.club.service.utils.impl;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.service.utils.FilterHtmlService;

@Service
public class FilterHtmlServiceImpl implements FilterHtmlService {
    @Override
    public String filterHtml(String comment) {
        Whitelist whitelist = new Whitelist();
        whitelist.addTags("h1", "h2", "h3", "h4", "h5", "ol", "li", "u", "s", "blockquote", "em", "strong", "i", "b", "p", "a", "br");
        whitelist.addAttributes(":all", "style");
        return Jsoup.clean(comment, whitelist);
    }
}
