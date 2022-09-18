package DuplicateDetection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TransitiveClosure {

    public static List<Duplicate> calculateTransitiveClosure(List<Duplicate> duplicates) {
        ArrayList<Duplicate> transitiveClosure = new ArrayList<>();
        for(int i = 0; i < duplicates.size(); i++) {
            Duplicate d1 = duplicates.get(i);
            for(int j = 0; j < duplicates.size(); j++) {
                Duplicate d2 = duplicates.get(j);
                if(i != j) {
                    if(d1.getId1().equals(d2.getId1())) {
                        Duplicate dNew = new Duplicate(d1.getId2(), d2.getId2());
                        if(!contains(transitiveClosure, dNew)) transitiveClosure.add(dNew);
                    }
                    if(d1.getId1().equals(d2.getId2())) {
                        Duplicate dNew = new Duplicate(d1.getId2(), d2.getId1());
                        if(!contains(transitiveClosure, dNew)) transitiveClosure.add(dNew);
                    }
                    if(d1.getId2().equals(d2.getId1())) {
                        Duplicate dNew = new Duplicate(d1.getId1(), d2.getId2());
                        if(!contains(transitiveClosure, dNew)) transitiveClosure.add(dNew);
                    }
                    if(d1.getId2().equals(d2.getId2())) {
                        Duplicate dNew = new Duplicate(d1.getId1(), d2.getId1());
                        if(!contains(transitiveClosure, dNew)) transitiveClosure.add(dNew);
                    }
                }
            }
        }
        //System.out.println("TransitiveClosure Duplicates:");
        //transitiveClosure.forEach(p -> System.out.println(p.toString()));
        transitiveClosure.addAll(duplicates);
        return transitiveClosure;
    }

    public static List<Duplicate> calculateTransitiveClosure(List<Duplicate> duplicates, List<Duplicate> goldStandard) {
        ArrayList<Duplicate> transitiveClosure = new ArrayList<>();
        for(int i = 0; i < duplicates.size(); i++) {
            Duplicate d1 = duplicates.get(i);
            for(int j = 0; j < duplicates.size(); j++) {
                Duplicate d2 = duplicates.get(j);
                if(i != j) {
                    if(d1.getId1().equals(d2.getId1())) {
                        Duplicate dNew = new Duplicate(d1.getId2(), d2.getId2());
                        if(!contains(transitiveClosure, dNew) && contains(goldStandard, dNew)) transitiveClosure.add(dNew);
                    }
                    if(d1.getId1().equals(d2.getId2())) {
                        Duplicate dNew = new Duplicate(d1.getId2(), d2.getId1());
                        if(!contains(transitiveClosure, dNew) && contains(goldStandard, dNew)) transitiveClosure.add(dNew);
                    }
                    if(d1.getId2().equals(d2.getId1())) {
                        Duplicate dNew = new Duplicate(d1.getId1(), d2.getId2());
                        if(!contains(transitiveClosure, dNew) && contains(goldStandard, dNew)) transitiveClosure.add(dNew);
                    }
                    if(d1.getId2().equals(d2.getId2())) {
                        Duplicate dNew = new Duplicate(d1.getId1(), d2.getId1());
                        if(!contains(transitiveClosure, dNew) && contains(goldStandard, dNew)) transitiveClosure.add(dNew);
                    }
                }
            }
        }
        //System.out.println("TransitiveClosure Duplicates:");
        //transitiveClosure.forEach(p -> System.out.println(p.toString()));
        transitiveClosure.addAll(duplicates);
        return transitiveClosure;
    }

    public static boolean contains(List<Duplicate> tc, Duplicate d) {
        for (int i = 0; i < tc.size(); i++) {
            if (tc.get(i).equals(d)) return true;
        }
        return false;
    }
}
