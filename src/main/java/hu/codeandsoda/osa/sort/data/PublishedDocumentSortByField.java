package hu.codeandsoda.osa.sort.data;

import java.text.RuleBasedCollator;
import java.util.Comparator;

import hu.codeandsoda.osa.documentpublish.domain.PublishedDocument;
import hu.codeandsoda.osa.sort.service.ComparatorUtil;

public enum PublishedDocumentSortByField {

    DATE((PublishedDocument p1, PublishedDocument p2) -> p1.getCreatedAt().compareTo(p2.getCreatedAt())),
    VIEWS((PublishedDocument p1, PublishedDocument p2) -> Integer.compare(p2.getViews(), p1.getViews())),
    TITLE(getNameComparator());

    private Comparator<PublishedDocument> comparator;

    private PublishedDocumentSortByField(Comparator<PublishedDocument> comparator) {
        this.comparator = comparator;
    }

    public Comparator<PublishedDocument> getComparator() {
        return comparator;
    }

    private static Comparator<PublishedDocument> getNameComparator() {
        Comparator<PublishedDocument> nameComparator = new Comparator<PublishedDocument>() {

            @Override
            public int compare(PublishedDocument p1, PublishedDocument p2) {
                int result = 0;

                RuleBasedCollator collator = ComparatorUtil.getHungarianCollator();
                if (null != collator) {
                    result = collator.compare(p2.getPublishedDocumentMetaData().getOriginalTitle(), p1.getPublishedDocumentMetaData().getOriginalTitle());
                } else {
                    result = p2.getPublishedDocumentMetaData().getOriginalTitle()
                            .compareToIgnoreCase(p1.getPublishedDocumentMetaData().getOriginalTitle());
                }
                return result;
            }
        };
        return nameComparator;
    }

}
