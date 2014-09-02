package org.agmip.ui.aceb2csv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.agmip.ace.AceDataset;
import org.agmip.ace.AceExperiment;
import org.agmip.ace.AceSoil;
import org.agmip.ace.AceWeather;
import org.agmip.ace.io.AceParser;
import org.agmip.core.types.TranslatorOutput;
import org.agmip.translators.csv.CSVOutput;
import org.agmip.util.JSONAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException {

        String acebFile;
        String outputDir;

        if (args.length < 1) {
            LOG.error("Not arguments to translate ACEB file to CSV file");
            return;
        }
        acebFile = args[0];
        if (args.length < 2) {
            outputDir = new File(acebFile).getParent();
        } else {
            outputDir = args[1];
        }
        LOG.info("Translating file: {}", acebFile);
        LOG.info("Output directory: {}", outputDir);

        // Load the ACE Binay file into memory and transform it to old JSON format and send it down the line.
        LOG.info("Loading the ACEB file...");
        AceDataset ace = AceParser.parseACEB(new File(acebFile));
        ace.linkDataset();
        HashMap data = new HashMap();
        ArrayList<HashMap> arr;
        // Experiments
        arr = new ArrayList();
        for (AceExperiment exp : ace.getExperiments()) {
            arr.add(JSONAdapter.fromJSON(new String(exp.rebuildComponent())));
        }
        if (!arr.isEmpty()) {
            data.put("experiments", arr);
        }
        // Soils
        arr = new ArrayList();
        for (AceSoil soil : ace.getSoils()) {
            arr.add(JSONAdapter.fromJSON(new String(soil.rebuildComponent())));
        }
        if (!arr.isEmpty()) {
            data.put("soils", arr);
        }
        // Weathers
        arr = new ArrayList();
        for (AceWeather wth : ace.getWeathers()) {
            arr.add(JSONAdapter.fromJSON(new String(wth.rebuildComponent())));
        }
        if (!arr.isEmpty()) {
            data.put("weathers", arr);
        }

        // Translate the data to csv file
        LOG.info("Translating the data...");
        TranslatorOutput output = new CSVOutput();
        output.writeFile(outputDir, data);
        LOG.info("Job done!");
    }
}
