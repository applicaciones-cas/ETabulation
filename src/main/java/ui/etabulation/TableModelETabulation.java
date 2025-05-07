package ui.etabulation;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TableModelETabulation {
    public static class Result {

        private final SimpleStringProperty candidates;
        private final SimpleIntegerProperty sportswear;
        private final SimpleIntegerProperty filipiniana;
        private final SimpleIntegerProperty talent;
        private final SimpleIntegerProperty personality;
        private final SimpleIntegerProperty beauty;
        private final SimpleIntegerProperty audience;
        public final SimpleIntegerProperty total; 
        private final SimpleStringProperty imageUrl;
        private final SimpleStringProperty school;  

        
        public Result(String candidates, int sportswear, int filipiniana, int talent,
                      int personality, int beauty, int audience, int dummyTotal, String imageUrl, String school) {
                      
            this.candidates = new SimpleStringProperty(candidates);
            this.sportswear = new SimpleIntegerProperty(sportswear);
            this.filipiniana = new SimpleIntegerProperty(filipiniana);
            this.talent = new SimpleIntegerProperty(talent);
            this.personality = new SimpleIntegerProperty(personality);
            this.beauty = new SimpleIntegerProperty(beauty);
            this.audience = new SimpleIntegerProperty(audience);
            this.total = new SimpleIntegerProperty(0);
            this.total.bind(
                this.sportswear.add(this.filipiniana)
                               .add(this.talent)
                               .add(this.personality)
                               .add(this.beauty)
                               .add(this.audience)
            );
            this.imageUrl = new SimpleStringProperty(imageUrl);
            this.school   = new SimpleStringProperty(school);
        }

        
        public String getCandidates() {
            return candidates.get();
        }
        public int getSportswear() {
            return sportswear.get();
        }
        public int getFilipiniana() {
            return filipiniana.get();
        }
        public int getTalent() {
            return talent.get();
        }
        public int getPersonality() {
            return personality.get();
        }
        public int getBeauty() {
            return beauty.get();
        }
        public int getAudience() {
            return audience.get();
        }
        public int getTotal() {
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
        public void setSportswear(int sportswear) {
            this.sportswear.set(sportswear);
        }
        public void setFilipiniana(int filipiniana) {
            this.filipiniana.set(filipiniana);
        }
        public void setTalent(int talent) {
            this.talent.set(talent);
        }
        public void setPersonality(int personality) {
            this.personality.set(personality);
        }
        public void setBeauty(int beauty) {
            this.beauty.set(beauty);
        }
        public void setAudience(int audience) {
            this.audience.set(audience);
        }
        
        public void setTotal(int total) {
            this.total.set(total);
        }
        public void setSchool(String school) {
            this.school.set(school);
        }
        
        public SimpleStringProperty schoolProperty() { return school; }

        
        public SimpleIntegerProperty totalProperty() {
            return total;
        }
    }
}
