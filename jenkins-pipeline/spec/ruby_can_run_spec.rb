require_relative './spec_helper'
require "commercial_cloud/test/terraform"
require "commercial_cloud/test/matcher/terraform"

include CommercialCloud::Test

# Verify terraform plan works
describe 'spec_runs' do


  describe "simple run" do
    it 'Verify nothing' do
      expect(true).to equal(true)
      #do nothing for now
    end
   

  end

end
