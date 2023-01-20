package hu.codeandsoda.osa.sort.data;

import java.text.RuleBasedCollator;
import java.util.Comparator;

import hu.codeandsoda.osa.myshoebox.data.ImageData;
import hu.codeandsoda.osa.sort.service.ComparatorUtil;

public enum MyShoeBoxSortByField {

    DATE((ImageData i1, ImageData i2) -> i1.getModifiedAt().compareTo(i2.getModifiedAt())),
    NAME(getNameComparator());

    private Comparator<ImageData> comparator;

    private MyShoeBoxSortByField(Comparator<ImageData> comparator) {
        this.comparator = comparator;
    }

    public Comparator<ImageData> getComparator() {
        return comparator;
    }

    private static Comparator<ImageData> getNameComparator() {
        Comparator<ImageData> nameComparator = new Comparator<ImageData>() {

            @Override
            public int compare(ImageData i1, ImageData i2) {
                int result = 0;

                RuleBasedCollator collator = ComparatorUtil.getHungarianCollator();
                if (null != collator) {
                    result = collator.compare(i2.getName(), i1.getName());
                } else {
                    result = i2.getName().compareToIgnoreCase(i1.getName());
                }
                return result;
            }
        };
        return nameComparator;
    }

}
