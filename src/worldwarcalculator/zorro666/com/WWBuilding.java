package worldwarcalculator.zorro666.com;

import android.widget.TextView;
import android.widget.EditText;

public class WWBuilding 
{
	public WWBuilding( final String name, final int baseCost, final int reward, final float valueMultiplier )
	{
		m_name = name;
		m_baseCostInK = baseCost/1000;
		m_reward = reward;
		m_numOwned = 0;
		m_valueMultiplier = valueMultiplier;
	}
	public String GetName()
	{
		return m_name;
	}
	
	public int GetBaseCost()
	{
		return m_baseCostInK * 1000;
	}
	
	public long GetCurrentCost()
	{
		long costInK = GetCurrentCostInK();
		long cost = costInK * 1000;
		return cost;
	}
	public int GetCurrentCostInK()
	{
		int costInK = m_baseCostInK + ( m_numOwned * m_baseCostInK ) / 10;
		return costInK;
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
		float rawValue = GetRawValue();
		float value = rawValue * m_valueMultiplier;
    	value = Math.round(value * 100.0f)/100.0f;
		return value;
	}
	public float GetRawValue()
	{
		int costInK = GetCurrentCostInK();
		int reward = GetReward();
		float value = costInK/(reward+0.0f);
		value *= 1000.0f;
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
	private int m_baseCostInK;
	private int m_reward;
	private int m_numOwned;
	private float m_valueMultiplier;;
	private EditText m_viewNumOwned;
	private TextView m_viewValue;
	private TextView m_viewCurrentCost;
}