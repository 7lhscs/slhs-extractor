package uil.extractor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Cleans raw horizontal segments into whole table borders.
 *
 * <p>Word-generated tables draw each cell border separately, so one visual border arrives as
 * several segments that share a y coordinate. This class joins them and discards short
 * segments such as hyperlink underlines.</p>
 */
public final class RuleMerger {

    private static final double Y_TOLERANCE = 1.0;
    private static final double GAP_TOLERANCE = 2.0;
    private static final double MIN_WIDTH_RATIO = 0.8;

    private RuleMerger() {
    }

    /**
     * Joins touching segments on the same line and keeps only table-wide borders.
     *
     * @param raw segments collected from a page, in any order
     * @return table borders ordered from the top of the page to the bottom
     */
    public static List<Rule> tableBorders(List<Rule> raw) {
        List<Rule> merged = new ArrayList<>();
        for (List<Rule> line : groupByLine(raw)) {
            merged.addAll(joinTouching(line));
        }
        double widest = merged.stream().mapToDouble(Rule::width).max().orElse(0);
        return merged.stream()
                .filter(rule -> rule.width() >= widest * MIN_WIDTH_RATIO)
                .sorted(Comparator.comparingDouble(Rule::y))
                .toList();
    }

    private static List<List<Rule>> groupByLine(List<Rule> raw) {
        List<Rule> sorted = raw.stream().sorted(Comparator.comparingDouble(Rule::y)).toList();
        List<List<Rule>> lines = new ArrayList<>();
        double anchorY = Double.NaN;
        for (Rule rule : sorted) {
            if (lines.isEmpty() || rule.y() - anchorY > Y_TOLERANCE) {
                lines.add(new ArrayList<>());
                anchorY = rule.y();
            }
            lines.get(lines.size() - 1).add(rule);
        }
        return lines;
    }

    private static List<Rule> joinTouching(List<Rule> line) {
        List<Rule> sorted = line.stream().sorted(Comparator.comparingDouble(Rule::left)).toList();
        double y = sorted.get(0).y();
        List<Rule> joined = new ArrayList<>();
        double left = sorted.get(0).left();
        double right = sorted.get(0).right();
        for (Rule rule : sorted.subList(1, sorted.size())) {
            if (rule.left() <= right + GAP_TOLERANCE) {
                right = Math.max(right, rule.right());
            } else {
                joined.add(new Rule(left, right, y));
                left = rule.left();
                right = rule.right();
            }
        }
        joined.add(new Rule(left, right, y));
        return joined;
    }
}
