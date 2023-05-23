package Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("status")
    private String status;

    @SerializedName("rows")
    private List<Row> rows;

    public class Row {

        @SerializedName("elements")
        private List<Element> elements;

        public class Element {

            @SerializedName("distance")
            private Distance distance;

            @SerializedName("duration")
            private Duration duration;

            @SerializedName("status")
            private String status;

            public class Distance {

                @SerializedName("text")
                private String text;

                @SerializedName("value")
                private int value;

                public String getText() {
                    return text;
                }
            }

            public class Duration {

                @SerializedName("text")
                private String text;

                @SerializedName("value")
                private int value;
            }

            public Distance getDistance() {
                return distance;
            }

            public Duration getDuration() {
                return duration;
            }

            public String getStatus() {
                return status;
            }
        }

        public List<Element> getElements() {
            return elements;
        }
    }

    public String getStatus() {
        return status;
    }

    public List<Row> getRows() {
        return rows;
    }
}
