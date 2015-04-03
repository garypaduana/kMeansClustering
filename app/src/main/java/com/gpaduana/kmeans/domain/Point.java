package com.gpaduana.kmeans.domain;

public class Point {
	
	private float x;
	private float y;
	private float z;
	
	/**
	 * If parent is not null, it means x, y, and z are used to represent color
	 * RGB values and the parent is the 3d positional information.
	 */
	private Point parent;
	
	public enum Type{
		DIMEN, COLOR;
	}
	
	public Point(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float getX(){
		return x;
	}
	
	public float getX(Type t){
		if(parent != null){
			if(t.equals(Type.DIMEN)){
				return parent.x;
			}
			else{
				return x;
			}
		}
		else{
			return x;
		}
	}
	
	public void setX(float x){
		this.x = x;
	}
	
	public float getY(){
		return y;
	}
	
	public float getY(Type t){
		if(parent != null){
			if(t.equals(Type.DIMEN)){
				return parent.y;
			}
			else{
				return y;
			}
		}
		else{
			return y;
		}
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
		else if(op.getZ() != this.z){
			return false;
		}
		else if(op.getParent() != null){
			if(op.getParent().equals(this)){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode(){
		return (int)(((int)x * 31) + ((int)y * 39)) + ((int)z * 41);
	}
	
	@Override
	public String toString(){
		return "(" + x + ", " + y + ", " + z + ")";
	}
	
	public String toString(Type t){
		return "(" + getX(t) + ", " + getY(t) + ", " + getZ(t) + ")";
	}

	public float getZ() {
		return z;
	}
	
	public float getZ(Type t){
		if(parent != null){
			if(t.equals(Type.DIMEN)){
				return parent.z;
			}
			else{
				return z;
			}
		}
		else{
			return z;
		}
	}

	public void setZ(float z) {
		this.z = z;
	}

	public Point getParent() {
		return parent;
	}

	public void setParent(Point parent) {
		this.parent = parent;
	}
}