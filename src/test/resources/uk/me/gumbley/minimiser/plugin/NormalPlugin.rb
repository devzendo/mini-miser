include Java

include_class 'uk.me.gumbley.minimiser.plugin.Plugin'
include_class 'uk.me.gumbley.minimiser.plugin.AbstractPlugin'

class NormalPlugin < AbstractPlugin
  def initialize() 
      @name = 'Normal'
      @version = '1.0.1'
      @schema_version = nil
    end
    
  attr_reader :name, :version, :schema_version
  def getName
    @name    
  end
  
  def getVersion
    @version    
  end
  
  def getSchemaVersion
    @schema_version    
  end
end
NormalPlugin.new
