package code.it;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.fluent.Request;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SubmitSolution {

    public static void main(String[] args) throws IOException, InterruptedException {

        List<String> lines = FileUtils.readLines(new File("./solution.txt"), "UTF-8");

        int index = 1;
        for (String direction : lines) {
            System.out.println("Step " + index + "  of " + lines.size());
            String runId = "233d698c-bc5d-43ed-bac5-c61bd9051736";
            String solutionUrl = "https://cis2017-warehouse-keeper.herokuapp.com/move/" + direction + "?run_id=" + runId;


            System.out.println("request " + solutionUrl);
            String response = Request.Get(solutionUrl)
                    .execute()
                    .returnContent()
                    .asString();
            System.out.println(response);

            Thread.sleep(10);

            System.out.println(solutionUrl);
            index++;

        }

    }
}
