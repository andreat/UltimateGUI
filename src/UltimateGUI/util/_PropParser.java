package UltimateGUI.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class _PropParser {
    private final Pattern prop_regex = Pattern.compile("^\\s*CHECK\\s*\\(\\s*init\\s*\\((.*)\\)\\s*,\\s*LTL\\((.*)\\)\\s*\\)\\s*$", Pattern.MULTILINE);
    private final Pattern funid_regex = Pattern.compile("\\s*(\\S*)\\s*\\(.*\\)");
    private final Pattern word_regex = Pattern.compile("\\b[^\\W\\d_]+\\b");
    private final String[] forbidden_words = {"valid-free", "valid-deref", "valid-memtrack", "end", "overflow", "call"};
    
    private final String content;
    private boolean termination;
    private boolean mem_deref;
    private boolean mem_memtrack;
    private boolean mem_free;
    private boolean overflow;
    private boolean reach;
    private boolean ltl;
    private String init;
    private String ltlformula;

    public _PropParser(File propfile) throws UltimateException {
        try(BufferedReader br = new BufferedReader(new FileReader(propfile))) {
        	StringBuilder sb = new StringBuilder();
        	String line;
        	while ((line = br.readLine()) != null) {
        		sb.append(line).append("\n");
        	}
        	content = sb.toString();
        } catch (IOException ioe) {
        	throw new UltimateException("General error in reading the property file", ioe);
        }
        termination = false;
        mem_deref = false;
        mem_memtrack = false;
        mem_free = false;
        overflow = false;
        reach = false;
        ltl = false;
        init = null;
        ltlformula = null;

        Matcher matcher_prop_regex = prop_regex.matcher(content);
        while (matcher_prop_regex.find()) {
            init = matcher_prop_regex.group(0); 
            String formula = matcher_prop_regex.group(1);

            Matcher matcher_funid_regex = funid_regex.matcher(init);
            if (matcher_funid_regex.find()) {
            	String otherinit = matcher_funid_regex.group();
            	if (init != null && !init.equals(otherinit))
            		throw new UltimateException("We do not support multiple and different init functions (have seen " + init + " and " + otherinit + ")");
            	else init = otherinit;
            } else {
            	throw new UltimateException("No init specified in this check");
            }

            if (formula.equals("G ! call(__VERIFIER_error())"))
                reach = true;
            else if (formula.equals("G valid-free"))
                mem_free = true;
            else if (formula.equals("G valid-deref"))
                mem_deref = true;
            else if (formula.equals("G valid-memtrack"))
                mem_memtrack = true;
            else if (formula.equals("F end"))
                termination = true;
    		else if (formula.equals("G ! overflow"))
                overflow = true;
    		else {
    			Matcher matcher_word_regex = word_regex.matcher(formula);
    			List<String> matched_word_regex = new LinkedList<String>();
    			while (matcher_word_regex.find()) {
    				matched_word_regex.add(matcher_word_regex.group());
    			}
    			if (!check_string_contains(matched_word_regex, this.forbidden_words)) {
	                // it is ltl
	                if (ltl) 
	                    throw new UltimateException("We support only one (real) LTL property per .prp file (have seen " + ltlformula + " and " + formula + ")");
	                ltl = true;
	                ltlformula = formula;
    			} else
    				throw new UltimateException("The formula " + formula + " is unknown");
    		}
        }
    }

    public String get_init_method() {
        return init;
    }

    public String get_content() {
        return content;
    }

    public boolean is_termination() {
        return termination;
    }

    public boolean is_only_mem_deref() {
        return mem_deref &&  !mem_free && !mem_memtrack;
    }

    public boolean is_any_mem() {
        return mem_deref || mem_free || mem_memtrack;
    }

    public boolean is_mem_deref_memtrack() {
        return mem_deref && mem_memtrack;
    }

    public boolean is_overflow() {
        return overflow;
    }

    public boolean is_reach() {
        return reach;
    }
    
    public boolean is_ltl() {
        return ltl;
    }

    public String get_ltl_formula() {
        return ltlformula;
    }
	
	private boolean check_string_contains(List<String> strings, String[] words) {
	    for (String string : strings) {
	        for (String word : words) {
	            if (word.equals(string)) 
	                return true;
	        }
	    }
	    return false;
	}
}
