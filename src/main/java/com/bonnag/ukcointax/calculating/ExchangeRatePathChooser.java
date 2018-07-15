package com.bonnag.ukcointax.calculating;

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
            double weight = score(assetPair.getBase()) * score(assetPair.getQuoted());
            graph.setEdgeWeight(edge, weight);
        }
        ShortestPathAlgorithm<Asset,DefaultEdge> shortestPathFinder = new DijkstraShortestPath<>(graph);
        if (!graph.containsVertex(Asset.Sterling)) {
            graph.addVertex(Asset.Sterling);
        }
        allPathsFromSterling = shortestPathFinder.getPaths(Asset.Sterling);
    }

    // Get weighting of an asset (should be near 1.0, lower good, higher bad).
    // We multiply the score of the two assets involved in the trade to decide
    // which exchange rates to use when there is a choice.
    // We choose the score so that we favour fiat-fiat over fiat-crypto over
    // crypto-crypto, and within those categories, we want to favour sterling
    // over others, and favour bitcoin over ether over others.
    // However, we still want to favour a shorter-path - so e.g. fiat-crypto
    // still beats fiat-fiat-fiat-fiat! (We assume paths are never more than
    // a few steps).
    // We also want to be deterministic across runs - running the program twice
    // should give the same tax figures, so we add a little final tie-breaker
    // quantity to the score.
    public double score(Asset asset) {
        return categoryScore(asset) + stabilityScore(asset);
    }

    public double categoryScore(Asset asset) {
        if (asset.isSterling()) {
            return 1.00;
        } else if (asset.isFiat()) {
            return 1.01;
        } else if (asset.getAssetCode().equals("BTC")) {
            return 1.02;
        } else if (asset.getAssetCode().equals("ETH")) {
            return 1.03;
        } else {
            return 1.04;
        }
    }

    // Avoid non-determinism - same set of exchange rate pairs should
    // always produce same paths regardless of ordering, so we fudge
    // the weights a little.
    public double stabilityScore(Asset asset) {
        // Java's hashCode algo has been documented for Strings since 1.2 so hopefully won't change
        double normalisedHashCode = Math.abs((double) asset.getAssetCode().hashCode() / (double) Integer.MIN_VALUE);
        return 1e-6 * normalisedHashCode;
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
                    Arrays.stream(assets).map(Asset::getAssetCode).collect(Collectors.joining(",")));
        }
        List<Asset> assetPath = new ArrayList<>(bestPath.getVertexList());
        Collections.reverse(assetPath);
        return assetPath;
    }
}
