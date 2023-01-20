package hu.codeandsoda.osa.sort.data;

import java.text.RuleBasedCollator;
import java.util.Comparator;

import hu.codeandsoda.osa.document.data.DocumentData;
import hu.codeandsoda.osa.sort.service.ComparatorUtil;

public enum DocumentSortByField {

    DATE((DocumentData d1, DocumentData d2) -> d1.getModifiedAt().compareTo(d2.getModifiedAt())),
    NAME(getNameComparator());

    private Comparator<DocumentData> comparator;

    private DocumentSortByField(Comparator<DocumentData> comparator) {
        this.comparator = comparator;
    }

    public Comparator<DocumentData> getComparator() {
        return comparator;
    }

    private static Comparator<DocumentData> getNameComparator() {
        Comparator<DocumentData> nameComparator = new Comparator<DocumentData>() {
            @Override
            public int compare(DocumentData d1, DocumentData d2) {
                int result = 0;

                RuleBasedCollator collator = ComparatorUtil.getHungarianCollator();
                if (d1.getMetaData().getOriginalTitle() == null && d2.getMetaData().getOriginalTitle() == null) {
                    result = 0;
                } else if (d1.getMetaData().getOriginalTitle() == null) {
                    result = -1;
                } else if (d2.getMetaData().getOriginalTitle() == null) {
                    result = 1;
                } else if (null != collator) {
                    result = collator.compare(d2.getMetaData().getOriginalTitle(), d1.getMetaData().getOriginalTitle());
                } else {
                    result = d2.getMetaData().getOriginalTitle().compareToIgnoreCase(d1.getMetaData().getOriginalTitle());
                }

                return result;
            }
        };
        return nameComparator;
    }

}
