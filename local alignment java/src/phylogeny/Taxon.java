package phylogeny;

import java.util.ArrayList;

/**
 * Simple node representing a taxon
 */
public class Taxon {
    /** the distance from my parent */
    public int distance;

    public String taxon;


    private ArrayList<Taxon> kids;

    public Taxon(int distance, String taxon) {
        this.distance = distance;
        this.taxon    = taxon;
    }
    public void addChild(Taxon taxon) {
        this.kids.add(taxon);
    }

    public void removeChild(Taxon taxon) {
        this.kids.remove(this.kids.indexOf(taxon));
    }

    public int numberOfChildren() {
        return this.kids.size();
    }
    public Taxon() {
        this.kids = new ArrayList<Taxon>();
    }


    public String toString() {
         return null;
    }

}
