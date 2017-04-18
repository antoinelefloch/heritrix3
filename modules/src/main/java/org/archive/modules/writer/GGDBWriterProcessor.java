/**
 *
 * Description: this class allows to store in mysql image links during a crawl.
 *              It does not output WARC data.
 *
 * Note: You can copy this source code, but you need to keep these 2 lines in your file header.
 * author:  https://github.com/antoinelefloch
 *
 */
package org.archive.modules.writer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.archive.modules.CrawlURI;
import org.archive.modules.ProcessResult;

/**
 * @author lefla
 *
 */
public class GGDBWriterProcessor extends WARCWriterProcessor {

    private static final Logger logger = Logger.getLogger(GGDBWriterProcessor.class.getName());

    static private GGDBConnect dbConnect;

    {
        dbConnect = new GGDBConnect();
        if (dbConnect.connect() == false) {
            logger.warning("********************  DB connection problem ");
        } else {
            logger.warning("********************  DB connection OK ");
        }
    }

    // ----------------------------------------------
    // * @param curi
    // * CrawlURI to process.
    //
    @Override
    protected ProcessResult innerProcessResult(CrawlURI puri) {
        CrawlURI curi = (CrawlURI) puri;
        String scheme = curi.getUURI().getScheme().toLowerCase();
        try {
            if (shouldWrite(curi)) {
                // return write(scheme, curi);
                return ggWrite(scheme, curi);
            } else {
                copyForwardWriteTagIfDupe(curi);
            }
        } catch (IOException e) {
            curi.getNonFatalFailures().add(e);
            logger.log(Level.SEVERE, "Failed write of Records: " + curi.toString(), e);
        }
        return ProcessResult.PROCEED;
    }

    // ----------------------------------------------
    protected ProcessResult ggWrite(final String lowerCaseScheme, final CrawlURI curi) throws IOException {

        try {
            // check HTTP content type
            if ((curi.getContentType() != null) && 
                            ( (curi.getContentType().compareToIgnoreCase("image/jpeg") == 0) ||
                            (curi.getContentType().compareToIgnoreCase("image/png") == 0) ||
                            (curi.getContentType().compareToIgnoreCase("audio/mpeg") == 0) ||
                            (curi.getContentType().compareToIgnoreCase("audio/x-wav") == 0) ||
                            (curi.getContentType().compareToIgnoreCase("video/mpeg") == 0))) {
                dbConnect.insert( /* curi.getContentType() */ curi.getVia().toString() /* original page */, curi.toString() /* url */ );
            }
        } catch (java.lang.NullPointerException e) {
            logger.log(Level.INFO, "************** " + e);
        }

        return checkBytesWritten();
    }
}
