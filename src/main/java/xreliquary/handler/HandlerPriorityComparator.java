package xreliquary.handler;

import java.util.Comparator;

public class HandlerPriorityComparator implements Comparator<IPrioritizedHandler> {
	@Override
	public int compare(IPrioritizedHandler o1, IPrioritizedHandler o2) {
		int ret = 10 * (o1.getPriority().ordinal() - o2.getPriority().ordinal());
		//1 os return for same value is required here to prevent treeset from considering two handlers being the same and not inserting them in set
		//noinspection ComparatorMethodParameterNotUsed
		return ret == 0 ? 1 : ret;
	}
}
