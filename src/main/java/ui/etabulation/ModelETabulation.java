// File: ModelETabulation.java
package ui.etabulation;

import java.net.URI;
import ui.etabulation.Conn;
import ui.etabulation.TableModelETabulation;
import ui.etabulation.TableModelETabulation.Result;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ModelETabulation {
    private Conn dbConnector;
    private String iniFilePath;

    public ModelETabulation(String iniFilePath) {
        this.iniFilePath = iniFilePath;
        this.dbConnector = new Conn(iniFilePath);
    }


    public List<Result> fetchCandidateResults() {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT " +
                     "  metaName.sGroupIDx, " +
                     "  metaName.sValuexxx AS candidateName, " +
                     "  metaSchool.sValuexxx AS candidateSchool, " +   // ← add
                     "  metaImage.sValuexxx AS imageUrl " +
                     "FROM Contest_Participants_Meta metaName " +
                     "JOIN Contest_Participants_Meta metaSchool " +
                     "  ON metaName.sGroupIDx = metaSchool.sGroupIDx " +
                     "  AND metaSchool.sMetaIDxx = '00002' " +         // ← school meta
                     "JOIN Contest_Participants_Meta metaImage " +
                     "  ON metaName.sGroupIDx = metaImage.sGroupIDx " +
                     "  AND metaImage.sMetaIDxx = '00003' " +
                     "WHERE metaName.sMetaIDxx = '00001'";

        try (Connection conn = dbConnector.getConnection("GGC_ETabulation");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name     = rs.getString("candidateName");
                String school   = rs.getString("candidateSchool");
                String remoteUrl = rs.getString("imageUrl");
                // e.g. "https://…/GF2025/campus/1/1.png"
                URI uri = URI.create(remoteUrl);
                String[] parts = uri.getPath().split("/"); 
                // parts = ["","img","GF2025","campus","1","1.png"]
                String folder   = parts[3];             // "campus"
                String contnum  = parts[4];
                String filename = parts[parts.length-1]; // "1.png"
                
                if ("babe".equals(folder)) {
                    folder = "bikerbabe";
                } else if ("bulilit".equals(folder)){
                    folder = "bulilit";
                } else if ("dream".equals(folder)){
                    folder = "dreamboy";
                } else {
                    folder = "campus";
                }
                
                String local    = "D:\\GGC_Maven_Systems\\images\\gfest\\" 
                                  + folder + "\\" + contnum + "-" + filename;
                Result r = new Result(name, 0,0,0,0,0,0, 0, local, school);
                results.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

}
