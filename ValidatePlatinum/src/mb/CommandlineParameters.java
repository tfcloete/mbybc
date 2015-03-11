package mb;

import com.beust.jcommander.*;
import com.beust.jcommander.internal.*;
import java.util.*;

/**
 *
 * @author Tertius
 */
public class CommandlineParameters {
    
    @Parameter
    public List<String> parameters = Lists.newArrayList();

    //@Parameter(names = { "-log", "-verbose" }, description = "Level of verbosity")
    //public Integer verbose = 1;

    //@Parameter(names = "-debug", description = "Debug mode")
    //public boolean debug = false;
    
    @Parameter(names = "-d", description = "Validate Platinum debtors.")
    public boolean ValidateDebtors = false;

    @Parameter(names = "-p", description = "Validate Platinum prices.")
    public boolean ValidatePrices = false;
    
    @Parameter(names = "-all", description = "Do all Validations")
    public boolean ValidateAll = false;

    @Parameter(names = "-s", description = "Print summary only")
    public boolean PrintSummaryOnly = false;

    @Parameter(names = {"-h", "--help"},  description = "Ouput this help")
    public boolean help = false;

}