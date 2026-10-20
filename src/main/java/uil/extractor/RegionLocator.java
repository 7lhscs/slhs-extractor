package uil.extractor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Combines question labels and table borders into the page area owned by each table row.
 *
 * <p>A row starts at the nearest table-wide border above a label and ends at the border that
 * starts the next row.</p>
 */
public final class RegionLocator {

    private static final double LABEL_INDENT_TOLERANCE = 15.0;

    private RegionLocator() {
    }

    /**
     * Locates every question row on one page.
     *
     * @param pageIndex zero-based page index
     * @param markers   labels found on the page, in any order
     * @param borders   table borders on the page, as returned by {@link RuleMerger#tableBorders}
     * @return one region per table row that contains a label, ordered top to bottom
     */
    public static List<QuestionRegion> locate(int pageIndex, List<Marker> markers, List<Rule> borders) {
        Map<Rule, List<Integer>> numbersByTop = groupByTopBorder(markers, borders);
        List<Rule> tops = new ArrayList<>(numbersByTop.keySet());
        List<QuestionRegion> regions = new ArrayList<>();
        for (int i = 0; i < tops.size(); i++) {
            Rule top = tops.get(i);
            double bottom = i + 1 < tops.size() ? tops.get(i + 1).y() : lowestBorder(borders);
            if (bottom > top.y()) {
                regions.add(new QuestionRegion(
                        numbersByTop.get(top), pageIndex, top.left(), top.y(), top.right(), bottom));
            }
        }
        return regions;
    }

    private static Map<Rule, List<Integer>> groupByTopBorder(List<Marker> markers, List<Rule> borders) {
        Map<Rule, List<Integer>> grouped = new LinkedHashMap<>();
        markers.stream()
                .sorted(Comparator.comparingDouble(Marker::y))
                .forEach(marker -> topBorder(marker, borders).ifPresent(
                        top -> grouped.computeIfAbsent(top, key -> new ArrayList<>()).add(marker.number())));
        return grouped;
    }

    private static Optional<Rule> topBorder(Marker marker, List<Rule> borders) {
        return borders.stream()
                .filter(rule -> rule.y() < marker.y())
                .filter(rule -> Math.abs(marker.x() - rule.left()) <= LABEL_INDENT_TOLERANCE)
                .max(Comparator.comparingDouble(Rule::y));
    }

    private static double lowestBorder(List<Rule> borders) {
        return borders.stream().mapToDouble(Rule::y).max().orElse(0);
    }
}
