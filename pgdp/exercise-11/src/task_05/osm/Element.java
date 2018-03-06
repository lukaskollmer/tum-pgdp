package task_05.osm;

import java.util.ArrayList;
import java.util.List;

public class Element {
    public static class Tag {
        public final String key;
        public final String value;

        Tag(String key, String value) {
            this.key = key;
            this.value = value;
        }


        @Override
        public String toString() {
            return String.format("<osm.Element.Tag key='%s' value='%s'>", key, value);
        }
    }

    public final Long id;

    public final List<Tag> tags = new ArrayList<>();


    Element(Long id) {
        this.id = id;
    }


    void addTag(Tag tag) {
        tags.add(tag);
    }


    boolean hasTagWithKey(String key) {
        for (Tag tag : tags) {
            if (tag.key.equals(key)) return true;
        }
        return false;
    }


    boolean hasTagWithKeyAndValue(String key, String value) {
        for (Tag tag : tags) {
            if (tag.key.equals(key) && tag.value.equals(value)) return true;
        }
        return false;
    }


    protected String toString_tags() {
        StringBuilder stringBuilder = new StringBuilder();

        if (tags.isEmpty()) {
            return stringBuilder.append("[]").toString();
        }

        stringBuilder.append("[ ");

        for (int i = 0; i < tags.size(); i++) {
            Tag tag = tags.get(i);

            stringBuilder
                    .append("['")
                    .append(tag.key)
                    .append("': '")
                    .append(tag.value)
                    .append("']");

            stringBuilder.append(i < tags.size() - 1 ? ", " : "");
        }

        return stringBuilder.append(" ]").toString();
    }

    @Override
    public String toString() {
        return String.format("<osm.Element id=%s tags=%s >", id, toString_tags());
    }
}
