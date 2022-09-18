package DuplicateDetection;

import java.util.Objects;

public class Duplicate {
    private String id1;
    private String id2;

    public Duplicate(String id1, String id2) {
        if(id1.compareTo(id2) <= 0) {
            this.id1 = id1;
            this.id2 = id2;
        } else {
            this.id1 = id2;
            this.id2 = id1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Duplicate duplicate = (Duplicate) o;
        return this.id1.equals(duplicate.id1) && this.id2.equals(duplicate.id2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id1, id2);
    }

    public String getId1() {
        return this.id1;
    }

    public String getId2() {
        return this.id2;
    }

    public boolean containsId(String id) {
        return (id.equals(this.id1) || id.equals(this.id2)) ? true : false;
    }

    public boolean equals(Duplicate other) {
        return (this.containsId(other.getId1()) && this.containsId(other.getId2())) ? true : false;
    }

    public String toString() {
        return id1 + "-" + id2;
    }
}
