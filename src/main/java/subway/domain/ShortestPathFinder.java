package subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.exception.SubwayException;

import java.util.List;

public class ShortestPathFinder {
    public static final String ERR_WRONG_TYPE_MSG = "해당 타입이 없습니다.";

    List<Station> stations;
    Sections sections;
    WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
    DijkstraShortestPath dijkstraShortestPath;

    public ShortestPathFinder(List<Station> stations, Sections sections) {
        this.stations = stations;
        this.sections = sections;
        graphVertexAndEdgeSetting();
    }

    public void setType(FindPathType type) {
        graphEdgeWeightSetting(type);
        computeShortestPath();
    }

    private void computeShortestPath() {
        dijkstraShortestPath = new DijkstraShortestPath(graph);
    }

    public int calculateShortestStationNumber(Station from, Station to) {
        List<Station> shortestPath = dijkstraShortestPath.getPath(from, to).getVertexList();
        return shortestPath.size();
    }

    public List<Station> getStationsOnPath(Station from, Station to) {
        return dijkstraShortestPath.getPath(from, to).getVertexList();
    }

    private void graphVertexAndEdgeSetting() {
        addVertex();
        addEdge(sections);
    }

    private void addVertex() {
        for (Station station : stations) {
            graph.addVertex(station);
        }
    }

    private void addEdge(Sections allSections) {
        for (Section section : sections.getUnmodifiableList()) {
            graph.addEdge(section.from(), section.to());
        }
    }

    private void graphEdgeWeightSetting(FindPathType type) {
        if (FindPathType.DISTANCE == type) {
            edgeWeightSettingForDistance(sections);
            return;
        }
        if (FindPathType.TIME == type) {
            edgeWeightSettingForTime(sections);
            return;
        }
        throw new SubwayException(ERR_WRONG_TYPE_MSG);
    }

    private void edgeWeightSettingForDistance(Sections sections) {
        for (Section section : sections.getUnmodifiableList()) {
            graph.setEdgeWeight(graph.getEdge(section.from(), section.to()), section.getDistance());
        }
    }

    private void edgeWeightSettingForTime(Sections sections) {
        for (Section section : sections.getUnmodifiableList()) {
            graph.setEdgeWeight(graph.getEdge(section.from(), section.to()), section.getTakenTime());
        }
    }
}
