require_relative './spec_helper'

describe 'terraform version' do

  it 'should be v0.10x or v0.11x' do
    version_output = `terraform --version`
    expect(version_output)
      .to match(/0\.(10|11)\.[0-9]+/)
  end

end