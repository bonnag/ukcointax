package com.bonnag.ukcointax.calculations;

import com.bonnag.ukcointax.domain.Asset;
import com.bonnag.ukcointax.domain.ExchangeRate;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ExchangeRatePathChooser {

    private final ShortestPathAlgorithm.SingleSourcePaths<Asset, DefaultEdge> allPathsFromSterling;

    public ExchangeRatePathChooser(List<ExchangeRate> exchangeRates) {
        DefaultUndirectedWeightedGraph<Asset,DefaultEdge> graph = new DefaultUndirectedWeightedGraph<>(DefaultEdge.class);
        for (AssetPair assetPair : exchangeRates.stream().map(er -> er.getAssetPair()).distinct().collect(Collectors.toList())) {
            for (Asset asset : assetPair.asArray()) {
                graph.addVertex(asset);
            }
            DefaultEdge edge = graph.addEdge(assetPair.getBase(), assetPair.getQuoted());
            long numFiatAssets = Arrays.stream(assetPair.asArray()).filter(a -> a.isFiat()).count();
            // in case of a tie, favour fiat-fiat over fiat-crypto over crypto-crypto
            double weight = 1.0 - numFiatAssets * 0.01;
            graph.setEdgeWeight(edge, weight);
        }
        ShortestPathAlgorithm<Asset,DefaultEdge> shortestPathFinder = new DijkstraShortestPath<>(graph);
        if (!graph.containsVertex(Asset.Sterling)) {
            graph.addVertex(Asset.Sterling);
        }
        allPathsFromSterling = shortestPathFinder.getPaths(Asset.Sterling);
    }

    public List<Asset> chooseBestPathToSterlingFromAmong(Asset... assets) {
        GraphPath<Asset,DefaultEdge> bestPath = null;
        for (Asset asset : assets) {
            GraphPath<Asset,DefaultEdge> path = allPathsFromSterling.getPath(asset);
            if (path == null) {
                continue;
            }
            if (bestPath == null || path.getWeight() < bestPath.getWeight()) {
                bestPath = path;
            }
        }
        if (bestPath == null) {
            throw new IllegalStateException("unable to find path to sterling from any of " +
                    Arrays.stream(assets).map(Asset::getAssetCode).collect(Collectors.joining()));
        }
        List<Asset> assetPath = new ArrayList<Asset>(bestPath.getVertexList());
        Collections.reverse(assetPath);
        return assetPath;
    }
}
