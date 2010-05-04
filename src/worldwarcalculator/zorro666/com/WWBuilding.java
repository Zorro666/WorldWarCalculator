package worldwarcalculator.zorro666.com;

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
	
	public void SetNumOwned( int numOwned )
	{
		m_numOwned = numOwned;
	}
	
	private String m_name;
	private int m_baseCost;
	private int m_reward;
	private int m_numOwned;
}
