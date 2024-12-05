package it.uniroma3.homework3.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.util.HashMap;
import java.util.Map;

public class AnalyzerFactory {
    private static Analyzer analyzer;

    /**
     * Creates a PerFieldAnalyzerWrapper with specific analyzers for each field.
     *
     * @return the configured Analyzer
     */
    public static Analyzer getAnalyzer() {
        if (analyzer == null) {
            Analyzer defaultAnalyzer = new StandardAnalyzer();
            Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
            perFieldAnalyzers.put("Caption", new StandardAnalyzer());
            perFieldAnalyzers.put("TableInfo", new CommaSeparatedAnalyzer());
            perFieldAnalyzers.put("Footnotes", new EnglishAnalyzer());
            perFieldAnalyzers.put("References", new EnglishAnalyzer());
            analyzer = new PerFieldAnalyzerWrapper(defaultAnalyzer, perFieldAnalyzers);
        }
        return analyzer;
    }

    public static class CommaSeparatedAnalyzer extends Analyzer {
        @Override
        protected TokenStreamComponents createComponents(String fieldName) {
            // Tokenizer separating text for each comma
            PatternTokenizer tokenizer = new PatternTokenizer("[,]", 0);
            return new TokenStreamComponents(tokenizer);
        }
        // Internal class for the tokenizer based on pattern
        class PatternTokenizer extends org.apache.lucene.analysis.util.CharTokenizer {
            private final String pattern;
            public PatternTokenizer(String pattern, int flags) {
                this.pattern = pattern;
            }
            @Override
            protected boolean isTokenChar(int c) {
                // This is how the tokenizer will separate text
                return !(Character.isWhitespace(c) || c == ',');
            }
        }
    }
}
