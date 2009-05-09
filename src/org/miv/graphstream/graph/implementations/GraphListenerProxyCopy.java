/*
 * This file is part of GraphStream.
 * 
 * GraphStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GraphStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GraphStream.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.miv.graphstream.graph.implementations;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.GraphListener;
import org.miv.graphstream.graph.GraphListenerProxy;
import org.miv.graphstream.graph.Node;

/**
 * Implementation of the GraphListenerProxy that merely copy the evolution of
 * a graph into another. 
 */
public class GraphListenerProxyCopy implements GraphListenerProxy
{
// Attribute

	/**
	 * The graph we observe.
	 */
	protected Graph inGraph;
	
	/**
	 * The graph that copies the observed graph.
	 */
	protected Graph outGraph;
	
// Construction

	/**
	 * New proxy that copies everything that happen in the input graph into the output graph.
	 * @param input The input graph.
	 * @param output The output graph.
	 */
	public GraphListenerProxyCopy( Graph input, Graph output )
	{
		this( input, output, true );
	}
	
	/**
	 * New proxy that copies everything that happen in the input graph into the output graph.
	 * @param input The input graph.
	 * @param output The output graph.
	 * @param replayGraph If true, and if the input graph already contains elements and attributes
	 *  they are first copied to the output graph.
	 */
	public GraphListenerProxyCopy( Graph input, Graph output, boolean replayGraph )
	{
		if( input == output )
			throw new RuntimeException( "input == output ???" );
		
		inGraph  = input;
		outGraph = output;
		
		inGraph.addGraphListener( this );
		
		if( replayGraph )
			replayTheGraph();
	}
	
	/**
	 * Copy everything from the input graph to the output graph.
	 */
	protected void replayTheGraph()
	{
		// Replay all attributes of the graph.

		Iterable<String> k = inGraph.getAttributeKeySet();

		if( k != null )
		{
			for( String key: k )
			{
				Object val = inGraph.getAttribute( key );
				graphAttributeAdded( inGraph.getId(), key, val );
			}
		}

		k = null;

		// Replay all nodes and their attributes.


		for( Node node: inGraph )
		{
			nodeAdded( inGraph.getId(), node.getId() );

			k = node.getAttributeKeySet();

			if( k != null )
			{
				for( String key: k )
				{
					Object val = node.getAttribute( key );
					nodeAttributeAdded( inGraph.getId(), node.getId(), key, val );
				}
			}
		}

		k = null;

		// Replay all edges and their attributes.

		for( Edge edge : inGraph.edgeSet() )
		{
			edgeAdded( inGraph.getId(), edge.getId(), edge.getNode0().getId(), edge.getNode1().getId(), edge.isDirected() );

			k = edge.getAttributeKeySet();

			if( k != null )
			{
				for( String key: k )
				{
					Object val = edge.getAttribute( key );
					edgeAttributeAdded( inGraph.getId(), edge.getId(), key, val );
				}
			}
		}
	}
	
// Access
	
// Command
	
	public void addGraphListener( GraphListener listener )
    {
		outGraph.addGraphListener( listener );
    }
	
	public void removeGraphListener( GraphListener listener )
    {
		outGraph.removeGraphListener( listener );
    }

	public void unregisterFromGraph()
    {
		inGraph.removeGraphListener( this );
    }

	public void checkEvents()
    {
		// NOP! This is a direct copy.
    }

// Commands -- GraphListener

	public void nodeAdded( String graphId, String nodeId )
    {
		outGraph.addNode( nodeId );
    }

	public void edgeAdded( String graphId, String edgeId, String fromId, String toId, boolean directed )
    {
		outGraph.addEdge( edgeId, fromId, toId, directed );
    }

	public void nodeRemoved( String graphId, String nodeId )
    {
		outGraph.removeNode( nodeId );
    }

	public void edgeRemoved( String graphId, String edgeId )
    {
		outGraph.removeEdge( edgeId );
    }
	
	public void graphCleared( String graphId )
	{
		outGraph.clear();
	}

	public void stepBegins( String graphId, double time )
	{
		outGraph.stepBegins( time );
	}

	public void edgeAttributeAdded( String graphId, String edgeId, String attribute, Object value )
    {
		Edge edge = outGraph.getEdge( edgeId );
		
		if( edge != null )
			edge.setAttribute( attribute, value );			
    }

	public void edgeAttributeChanged( String graphId, String edgeId, String attribute, Object oldValue, Object newValue )
    {
		Edge edge = outGraph.getEdge( edgeId );
		
		if( edge != null )
			edge.changeAttribute( attribute, newValue );			
    }

	public void edgeAttributeRemoved( String graphId, String edgeId, String attribute )
    {
		Edge edge = outGraph.getEdge( edgeId );
		
		if( edge != null )
			edge.removeAttribute( attribute );			
    }

	public void graphAttributeAdded( String graphId, String attribute, Object value )
    {
		outGraph.setAttribute( attribute, value );
    }

	public void graphAttributeChanged( String graphId, String attribute, Object oldValue, Object newValue )
    {
		outGraph.changeAttribute( attribute, newValue );
    }

	public void graphAttributeRemoved( String graphId, String attribute )
    {
		outGraph.removeAttribute( attribute );
    }

	public void nodeAttributeAdded( String graphId, String nodeId, String attribute, Object value )
    {
		Node node = outGraph.getNode( nodeId );
			
		if( node != null )
			node.setAttribute( attribute, value );
    }

	public void nodeAttributeChanged( String graphId, String nodeId, String attribute, Object oldValue, Object newValue )
    {
		Node node = outGraph.getNode( nodeId );
		
		if( node != null )
			node.changeAttribute( attribute, newValue );
    }

	public void nodeAttributeRemoved( String graphId, String nodeId, String attribute )
    {
		Node node = outGraph.getNode( nodeId );
		
		if( node != null )
			node.removeAttribute( attribute );
    }
}