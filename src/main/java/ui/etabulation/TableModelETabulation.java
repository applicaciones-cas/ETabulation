package ui.etabulation;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableModelETabulation {

    private StringProperty index01;
    private StringProperty index02;
    private StringProperty index03;
    private StringProperty index04;
    private StringProperty index05;
    private StringProperty index06;
    private StringProperty index07;
    private StringProperty index08;
    private StringProperty index09;
    private StringProperty index10;

    public TableModelETabulation() {
        this.index01 = new SimpleStringProperty("");
        this.index02 = new SimpleStringProperty("");
        this.index03 = new SimpleStringProperty("");
        this.index04 = new SimpleStringProperty("");
        this.index05 = new SimpleStringProperty("");
        this.index06 = new SimpleStringProperty("");
        this.index07 = new SimpleStringProperty("");
        this.index08 = new SimpleStringProperty("");
        this.index09 = new SimpleStringProperty("");
        this.index10 = new SimpleStringProperty("");
    }

    TableModelETabulation(String index01, String index02, String index03, String index04, String index05,
            String index06, String index07, String index08, String index09, String index10) {
        this.index01 = new SimpleStringProperty(index01);
        this.index02 = new SimpleStringProperty(index02);
        this.index03 = new SimpleStringProperty(index03);
        this.index04 = new SimpleStringProperty(index04);
        this.index05 = new SimpleStringProperty(index05);
        this.index06 = new SimpleStringProperty(index06);
        this.index07 = new SimpleStringProperty(index07);
        this.index08 = new SimpleStringProperty(index08);
        this.index09 = new SimpleStringProperty(index09);
        this.index10 = new SimpleStringProperty(index10);
    }

    TableModelETabulation(String index01, String index02, String index03) {
        this.index01 = new SimpleStringProperty(index01);
        this.index02 = new SimpleStringProperty(index02);
        this.index03 = new SimpleStringProperty(index03);

    }

    public String getIndex01() {
        return index01.get();
    }

    public void setIndex01(String index01) {
        this.index01.set(index01);
    }

    public String getIndex02() {
        return index02.get();
    }

    public void setIndex02(String index02) {
        this.index02.set(index02);
    }

    public String getIndex03() {
        return index03.get();
    }

    public void setIndex03(String index03) {
        this.index03.set(index03);
    }

    public String getIndex04() {
        return index04.get();
    }

    public void setIndex04(String index04) {
        this.index04.set(index04);
    }

    public String getIndex05() {
        return index05.get();
    }

    public void setIndex05(String index05) {
        this.index05.set(index05);
    }

    public String getIndex06() {
        return index06.get();
    }

    public void setIndex06(String index06) {
        this.index06.set(index06);
    }

    public String getIndex07() {
        return index07.get();
    }

    public void setIndex07(String index07) {
        this.index07.set(index07);
    }

    public String getIndex08() {
        return index08.get();
    }

    public void setIndex08(String index08) {
        this.index08.set(index08);
    }

    public String getIndex09() {
        return index09.get();
    }

    public void setIndex09(String index09) {
        this.index09.set(index09);
    }

    public String getIndex10() {
        return index10.get();
    }

    public void setIndex10(String index10) {
        this.index10.set(index10);
    }

    public StringProperty index01Property() {
        return index01;
    }

    public StringProperty index02Property() {
        return index02;
    }

    public StringProperty index03Property() {
        return index03;
    }

    public StringProperty index04Property() {
        return index04;
    }

    public StringProperty index05Property() {
        return index05;
    }

    public StringProperty index06Property() {
        return index06;
    }

    public static class Result {

        private final SimpleStringProperty candidates;
        private final SimpleDoubleProperty sportswear;
        private final SimpleDoubleProperty filipiniana;
        private final SimpleDoubleProperty talent;
        private final SimpleDoubleProperty personality;
        private final SimpleDoubleProperty beauty;
        private final SimpleDoubleProperty audience;
        public final SimpleDoubleProperty total;
        private final SimpleStringProperty imageUrl;
        private final SimpleStringProperty school;
        private final SimpleStringProperty groupid;

        public Result(String candidates, double sportswear, double filipiniana, double talent,
                double personality, double beauty, double audience, double dummyTotal, String imageUrl, String school, String groupid) {

            this.candidates = new SimpleStringProperty(candidates);
            this.sportswear = new SimpleDoubleProperty(sportswear);
            this.filipiniana = new SimpleDoubleProperty(filipiniana);
            this.talent = new SimpleDoubleProperty(talent);
            this.personality = new SimpleDoubleProperty(personality);
            this.beauty = new SimpleDoubleProperty(beauty);
            this.audience = new SimpleDoubleProperty(audience);
            this.total = new SimpleDoubleProperty(dummyTotal);
            this.imageUrl = new SimpleStringProperty(imageUrl);
            this.school = new SimpleStringProperty(school);
            this.groupid = new SimpleStringProperty(groupid);
        }

        public String getCandidates() {
            return candidates.get();
        }

        public double getSportswear() {
            return sportswear.get();
        }

        public double getFilipiniana() {
            return filipiniana.get();
        }

        public double getTalent() {
            return talent.get();
        }

        public double getPersonality() {
            return personality.get();
        }

        public double getBeauty() {
            return beauty.get();
        }

        public double getAudience() {
            return audience.get();
        }

        public double getTotal() {
            return total.get();
        }

        public String getImageUrl() {
            return imageUrl.get();
        }

        public String getSchool() {
            return school.get();
        }

        public String getGroupID() {
            return groupid.get();
        }

        public void setCandidates(String candidates) {
            this.candidates.set(candidates);
        }

        public void setSportswear(double sportswear) {
            this.sportswear.set(sportswear);
        }

        public void setFilipiniana(double filipiniana) {
            this.filipiniana.set(filipiniana);
        }

        public void setTalent(double talent) {
            this.talent.set(talent);
        }

        public void setPersonality(double personality) {
            this.personality.set(personality);
        }

        public void setBeauty(double beauty) {
            this.beauty.set(beauty);
        }

        public void setAudience(double audience) {
            this.audience.set(audience);
        }

        public void setTotal(double total) {
            this.total.set(total);
        }

        public void setSchool(String school) {
            this.school.set(school);
        }

        public void setGroupID(String groupid) {
            this.groupid.set(groupid);
        }

        public SimpleStringProperty schoolProperty() {
            return school;
        }

        public SimpleDoubleProperty totalProperty() {
            return total;
        }
    }
}
