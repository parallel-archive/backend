package hu.codeandsoda.osa.documentfilter.data;

public class PeriodFilterData {

    private Integer from;

    private Integer to;

    public PeriodFilterData() {
    }

    private PeriodFilterData(PeriodFilterDataBuilder periodFilterDataBuilder) {
        from = periodFilterDataBuilder.from;
        to = periodFilterDataBuilder.to;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public static class PeriodFilterDataBuilder {

        private Integer from;

        private Integer to;

        public PeriodFilterDataBuilder setFrom(Integer from) {
            this.from = from;
            return this;
        }

        public PeriodFilterDataBuilder setTo(Integer to) {
            this.to = to;
            return this;
        }

        public PeriodFilterData build() {
            return new PeriodFilterData(this);
        }
    }

}
