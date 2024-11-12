package net.media.keywordlearning.Utils;

import com.google.common.base.Function;
import net.media.keywordlearning.beans.PirateDebugResult;

import java.util.Comparator;

/**
 * Created by autoopt/rohit.aga.
 */
public class ComparatorFactory {
    public static Comparator<PirateDebugResult> cmpPirateDebugResult(Function<PirateDebugResult, Double> functionsExtractor) {
        return (pirateDebugResult1, pirateDebugResult2) -> {
            double pirateDebug1 = functionsExtractor.apply(pirateDebugResult1);
            double pirateDebug2 = functionsExtractor.apply(pirateDebugResult2);
            if (pirateDebug1 == pirateDebug2) return 0;
            else return pirateDebug1 < pirateDebug2 ? 1 : -1;
        };
    }
}
