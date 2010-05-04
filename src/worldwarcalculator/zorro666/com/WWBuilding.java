package worldwarcalculator.zorro666.com;

import android.widget.TextView;
import android.widget.EditText;

public class WWBuilding 
{
	public WWBuilding( final String name, final int baseCost, final int reward )
	{
		m_name = name;
		m_baseCost = baseCost;
		m_reward = reward;
		m_numOwned = 0;
	}
	public String GetName()
	{
		return m_name;
	}
	
	public int GetBaseCost()
	{
		return m_baseCost;
	}
	
	public int GetCurrentCost()
	{
		int cost = m_baseCost + ( m_numOwned * m_baseCost ) / 10;
		return cost;
	}
	
	public int GetReward()
	{
		return m_reward;
	}
	
	public int GetNumOwned()
	{
		return m_numOwned;
	}
	public float GetValue()
	{
		int cost = GetCurrentCost();
		int reward = GetReward();
		float value = cost/(reward+0.0f);
    	value = Math.round(value * 100.0f)/100.0f;
		return value;
	}
	
	public EditText GetViewNumOwned()
	{
		return m_viewNumOwned;
	}
	public TextView GetViewValue()
	{
		return m_viewValue;
	}
	public TextView GetViewCurrentCost()
	{
		return m_viewCurrentCost;
	}
	
	public void SetNumOwned( int numOwned )
	{
		m_numOwned = numOwned;
	}
	
	public void SetViewNumOwned(EditText viewNumOwned)
	{
		m_viewNumOwned = viewNumOwned;
	}
	public void SetViewValue(TextView viewValue)
	{
		m_viewValue = viewValue;
	}
	public void SetViewCurrentCost(TextView viewCurrentCost)
	{
		m_viewCurrentCost = viewCurrentCost;
	}
	
	private String m_name;
	private int m_baseCost;
	private int m_reward;
	private int m_numOwned;
	private EditText m_viewNumOwned;
	private TextView m_viewValue;
	private TextView m_viewCurrentCost;
}