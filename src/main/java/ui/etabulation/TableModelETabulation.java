package ui.etabulation;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TableModelETabulation {
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

        
        public Result(String candidates, int sportswear, int filipiniana, int talent,
                      int personality, int beauty, int audience, int dummyTotal, String imageUrl, String school) {
                      
            this.candidates = new SimpleStringProperty(candidates);
            this.sportswear = new SimpleDoubleProperty(sportswear);
            this.filipiniana = new SimpleDoubleProperty(filipiniana);
            this.talent = new SimpleDoubleProperty(talent);
            this.personality = new SimpleDoubleProperty(personality);
            this.beauty = new SimpleDoubleProperty(beauty);
            this.audience = new SimpleDoubleProperty(audience);
//            this.total = new SimpleIntegerProperty(0);
//            this.total.bind(
//                this.sportswear.add(this.filipiniana)
//                               .add(this.talent)
//                               .add(this.personality)
//                               .add(this.beauty)
//                               .add(this.audience)
//            );
            this.total = new SimpleDoubleProperty(dummyTotal);
            this.imageUrl = new SimpleStringProperty(imageUrl);
            this.school   = new SimpleStringProperty(school);
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
        
        public SimpleStringProperty schoolProperty() { return school; }

        
        public SimpleDoubleProperty totalProperty() {
            return total;
        }
    }
}
