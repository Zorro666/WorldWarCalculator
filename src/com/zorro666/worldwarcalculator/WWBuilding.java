package com.zorro666.worldwarcalculator;

import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;


public class WWBuilding 
{
	public WWBuilding( final String name, final int baseCost, final int reward, final float cheapnessMultiplier )
	{
		m_name = name;
		m_baseCost = baseCost/BASE_COST_DIVISOR;
		m_reward = reward;
		m_cheapnessMultiplier = cheapnessMultiplier;
	}
	public String GetName()
	{
		return m_name;
	}
	
	public long GetBaseCost()
	{
		return m_baseCost*100;
	}
	
	public long GetCurrentCost(int numOwned)
	{
		long cost = m_baseCost + (long)numOwned * ( m_baseCost / (long)10 );
		cost *= BASE_COST_DIVISOR;
		return cost;
	}
	
	public int GetReward()
	{
		return m_reward;
	}
	
	public float GetCheapness(int numOwned)
	{
		float rawCheapness = GetRawCheapness(numOwned);
		float cheapness = rawCheapness * m_cheapnessMultiplier;
    	cheapness = Math.round(cheapness * 100.0f)/100.0f;
		return cheapness;
	}
	public float GetRawCheapness(int numOwned)
	{
		long cost = GetCurrentCost(numOwned);
		int reward = GetReward();
		float cheapness = cost/(reward+0.0f);
		cheapness *= BASE_COST_DIVISOR;
		return cheapness;
	}
	
	public EditText GetViewNumOwned()
	{
		return m_viewNumOwned;
	}
	public TextView GetViewCheapness()
	{
		return m_viewCheapness;
	}
	public TextView GetViewCurrentCost()
	{
		return m_viewCurrentCost;
	}
	
	public Button GetViewMinusButton()
	{
		return m_viewMinusButton;
	}
	public Button GetViewPlusButton()
	{
		return m_viewPlusButton;
	}
	public void SetViewMinusButton(Button viewMinusButton)
	{
		m_viewMinusButton = viewMinusButton;
	}
	public void SetViewPlusButton(Button viewPlusButton)
	{
		m_viewPlusButton = viewPlusButton;
	}
	public void SetViewNumOwned(EditText viewNumOwned)
	{
		m_viewNumOwned = viewNumOwned;
	}
	public void SetViewCheapness(TextView viewCheapness)
	{
		m_viewCheapness = viewCheapness;
	}
	public void SetViewCurrentCost(TextView viewCurrentCost)
	{
		m_viewCurrentCost = viewCurrentCost;
	}
	
	private final int BASE_COST_DIVISOR=100;
	private String m_name;
	private long m_baseCost;
	private int m_reward;
	private float m_cheapnessMultiplier;
	private Button m_viewMinusButton;
	private Button m_viewPlusButton;
	private EditText m_viewNumOwned;
	private TextView m_viewCheapness;
	private TextView m_viewCurrentCost;
}