package com.gpaduana.kmeans.domain;

/**
 * Models a point in (x, y) space.
 */
public class Point {
	
	private float x;
	private float y;

	public Point(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public float getX(){
		return x;
	}
	
	public void setX(float x){
		this.x = x;
	}
	
	public float getY(){
		return y;
	}
	
	public void setY(float y){
		this.y = y;		
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null){
			return false;
		}
		else if(!(o instanceof Point)){
			return false;
		}
		Point op = (Point)o;
		
		if(op.getX() != this.x){
			return false;
		}
		else if(op.getY() != this.y){
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode(){
		return (int)(((int)x * 31) + ((int)y * 39));
	}
	
	@Override
	public String toString(){
		return "(" + x + ", " + y + ")";
	}
}